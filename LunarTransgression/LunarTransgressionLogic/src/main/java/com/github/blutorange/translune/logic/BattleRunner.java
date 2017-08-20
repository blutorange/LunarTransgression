package com.github.blutorange.translune.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.websocket.Session;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;

import com.github.blutorange.translune.CustomProperties;
import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.message.MessageBattleCancelled;
import com.github.blutorange.translune.socket.message.MessageBattleEnded;
import com.github.blutorange.translune.socket.message.MessageBattlePrepared;
import com.github.blutorange.translune.socket.message.MessageBattleStepped;

public class BattleRunner implements IBattleRunner {
	private boolean battleDone;

	private final List<String[]> characterStates;

	private final List<IBattleCommand@Nullable[]> commands;

	private final List<String[]> items;

	private final Phaser phaser;

	@SuppressWarnings("null")
	private final @NonNull String[] players = new String[]{StringUtils.EMPTY,StringUtils.EMPTY};

	private int round;

	@Inject
	IBattleProcessing battleProcessing;

	@Inject
	CustomProperties customProperties;

	@Inject
	IBattleStore battleStore;

	@Inject
	ILunarDatabaseManager databaseManager;

	@Inject
	@Classed(BattleRunner.class)
	Logger logger;

	@Inject
	ISessionStore sessionStore;

	@Inject
	ISocketProcessing socketProcessing;

	@Inject
	public BattleRunner() {
		round = 0;
		characterStates = new CopyOnWriteArrayList<>(new String[2][]);
		items = new CopyOnWriteArrayList<>(new String[2][]);
		commands = Collections.synchronizedList(new ArrayList<>(2));
		phaser = new Phaser(2);
	}

	@Override
	public void forPlayers(final String from, final String to) {
		synchronized (players) {
			players[0] = from;
			players[1] = to;
		}
	}

	@Override
	public void preparePlayer(final String nickname, final String[] characterStates, final String[] items)
			throws IllegalStateException, IllegalArgumentException {
		synchronized (phaser) {
			if (phaser.isTerminated())
				return;
			final int playerIndex = getPlayerIndex(nickname);
			if (this.characterStates.get(playerIndex) != null)
				throw new IllegalStateException("Player is already prepared");
			if (characterStates.length != 4)
				throw new IllegalArgumentException("Player must choose exactly 4 characters.");
			if (items.length > 3)
				throw new IllegalArgumentException("Player must not choose more than 3 items.");
			assertBattlePrep(playerIndex, characterStates, items);
			this.characterStates.set(playerIndex, characterStates);
			this.items.set(playerIndex, items);
			phaser.arriveAndAwaitAdvance();
		}
	}

	@Override
	public void battlePlayer(final String nickname, final IBattleCommand character1, final IBattleCommand character2,
			final IBattleCommand character3, final IBattleCommand character4)
			throws IllegalStateException, IllegalArgumentException {
		synchronized (phaser) {
			if (phaser.isTerminated())
				return;
			final int playerIndex = getPlayerIndex(nickname);
			if (commands.get(playerIndex) != null)
				throw new IllegalStateException("Player already finalized the current turn.");
			final IBattleCommand[] cmds = new IBattleCommand[] { character1, character2, character3, character4 };
			assertBattleStep(playerIndex, cmds);
			commands.set(playerIndex, cmds);
			phaser.arriveAndAwaitAdvance();
		}
	}

	private void assertBattleStep(final int playerIndex, final IBattleCommand[] commands) {
		final String[] charStates = characterStates.get(playerIndex);
		for (int i = 4; i --> 0; ++i) {
			final CharacterState charState = databaseManager.find(CharacterState.class, charStates[i]);
			if (charState == null)
				throw new IllegalArgumentException(String.format("Character %s state does not exist", Integer.valueOf(i+1)));
			if (!commands[i].appliesTo(charState))
				throw new IllegalArgumentException(String.format("Command does not apply to character %s.", Integer.valueOf(i+1)));
		}
	}

	@Override
	public void run() {
		synchronized (players) {
			if (players[0].isEmpty() || players[1].isEmpty())
				throw new IllegalStateException("Battle players are not set.");
		}
		while (!battleDone) {
			if (round == 0)
				battlePreparations();
			else
				battleStep();
			++round;
		}
	}

	private void assertBattlePrep(final int playerIndex, final String[] characters, final String[] items) {
		final Player player = databaseManager.find(Player.class, players[playerIndex]);
		if (player == null)
			throw new IllegalStateException("Player does not exist");
		for (final String character : characters)
			if (!player.getCharacterStatesUnmodifiable().contains(databaseManager.find(CharacterState.class, character)))
				throw new IllegalArgumentException("Player does not own this character: " + character);
		for (final String item : items)
			if (!player.getItemsUnmodifiable().contains(databaseManager.find(Item.class, item)))
				throw new IllegalArgumentException("Player does not own this item: " + item);
//		for (final String character : characters)
//			for (final CharacterState cs : player.getCharacterStatesUnmodifiable())
//				if (!character.equals(cs.getPrimaryKey()))
//					throw new IllegalArgumentException("Player does not own this character: " + character);
//		for (final String item : items)
//			for (final Item i : player.getItemsUnmodifiable())
//				if (!item.equals(i.getPrimaryKey()))
//					throw new IllegalArgumentException("Player does not own this item: " + item);
	}

	private void battlePreparations() {
		try {
			phaser.awaitAdvanceInterruptibly(phaser.getPhase(), customProperties.getBattlePreparationTimeoutMillis(),
					TimeUnit.MILLISECONDS);
		}
		catch (final InterruptedException e) {
			logger.error("battle preparations interrupted", e);
			cancelBattle("Internal server error");
			return;
		}
		catch (final TimeoutException e) {
			logger.info("battle preparations timed out", e);
		}
		boolean player1Done = true;
		boolean player2Done = true;
		synchronized (phaser) {
			// Check if both players responded in time with a character/item
			// select.
			if (characterStates.get(0) == null) {
				player1Done = false;
			}
			if (characterStates.get(1) == null) {
				player2Done = false;
			}
		}
		if (!player1Done || !player2Done) {
			cancelBattle("The other player did not complete battle preparations in time.");
			return;
		}
		informPlayerAboutPrepared(0);
		informPlayerAboutPrepared(1);
		setGameStateForBoth(EGameState.IN_BATTLE, EGameState.IN_BATTLE);
	}

	private void setGameStateForBoth(final EGameState state1, final EGameState state2) {
		@SuppressWarnings("resource")
		final Session session1 = sessionStore.retrieve(players[0]);
		@SuppressWarnings("resource")
		final Session session2 = sessionStore.retrieve(players[1]);
		if (session1 != null)
			synchronized (session1) {
				socketProcessing.setGameState(session1, state1);
			}
		if (session2 != null)
			synchronized (session2) {
				socketProcessing.setGameState(session2, state2);
			}
	}

	private void battleStep() {
		try {
			phaser.awaitAdvanceInterruptibly(phaser.getPhase(), customProperties.getBattleStepTimeoutMillis(),
					TimeUnit.MILLISECONDS);
		}
		catch (final InterruptedException e) {
			logger.error("battle step was interrupted", e);
			cancelBattle("Internal server error");
			return;
		}
		catch (final TimeoutException e) {
			logger.info("battle step timed out", e);
		}
		boolean player1Done = true;
		boolean player2Done = true;
		synchronized (phaser) {
			// Check if both players responded in time with a character/item
			// select.
			if (commands.get(0) == null) {
				player1Done = false;
			}
			if (commands.get(1) == null) {
				player2Done = false;
			}
		}
		if (!player1Done || !player2Done) {
			cancelBattle("The other player did enter battle commands in time.");
			return;
		}
		processBattle();
		checkBattleEnd();
	}

	private void checkBattleEnd() {
		final int winner = battleProcessing.checkBattleEnd(characterStates);
		if (winner >= 0) {
				battleDone = true;
				battleStore.addLoot(players[winner], characterStates.get(winner), items.get(winner));
				endBattle();
				if (winner == 0)
					setGameStateForBoth(EGameState.BATTLE_LOOT, EGameState.IN_BATTLE);
				else
					setGameStateForBoth(EGameState.IN_BATTLE, EGameState.BATTLE_LOOT);
				informPlayerAboutEnd(0, winner == 0);
				informPlayerAboutEnd(0, winner == 1);
		}
	}

	private void processBattle() {
		final List<@NonNull IBattleCommand[]> c = (List<IBattleCommand[]>) commands;
		final IBattleAction[][] battleResults = battleProcessing.simulateBattleStep(c, players, characterStates, items);
		clearCommands();
		informPlayerAboutStepped(0, battleResults);
		informPlayerAboutStepped(1, battleResults);
	}

	private void clearCommands() {
		synchronized (phaser) {
			commands.set(0, null);
			commands.set(1, null);
		}
	}

	private void endBattle() {
		battleDone = true;
		synchronized (phaser) {
			characterStates.clear();
			commands.clear();
			items.clear();
			// We already checked for null when battle processing began.
			battleStore.removeBattle(players[0], players[1]);
			phaser.forceTermination();
		}
	}

	private void cancelBattle(final String message) {
		endBattle();
		setGameStateForBoth(EGameState.IN_MENU, EGameState.IN_BATTLE);
		informPlayerAboutCancel(0, message);
		informPlayerAboutCancel(1, message);
	}

	private int getPlayerIndex(final String nickname) {
		if (players[0].equals(nickname))
			return 0;
		if (players[1].equals(nickname))
			return 1;
		throw new IllegalArgumentException("Player does not participate in this battle: " + nickname);
	}

	@SuppressWarnings("resource")
	private void informPlayerAboutCancel(final int playerIndex, final String message) {
		// We already checked when battle processing began.
		final Session session = sessionStore.retrieve(players[playerIndex]);
		if (session != null)
			socketProcessing.dispatchMessage(session, ELunarStatusCode.OK, new MessageBattleCancelled(message));
	}

	private void informPlayerAboutEnd(final int playerIndex, final boolean isVictory) {
		// We already checked when battle processing began.
		@SuppressWarnings("resource")
		final Session session = sessionStore.retrieve(players[playerIndex]);
		if (session != null)
			socketProcessing.dispatchMessage(session, ELunarStatusCode.OK, new MessageBattleEnded(isVictory));
	}

	private void informPlayerAboutPrepared(final int playerIndex) {
		// We already checked when battle processing began.
		@SuppressWarnings("resource")
		final Session session = sessionStore.retrieve(players[playerIndex]);
		if (session != null)
			socketProcessing.dispatchMessage(session, ELunarStatusCode.OK, new MessageBattlePrepared());
	}

	private void informPlayerAboutStepped(final int playerIndex, final IBattleAction[][] battleResults) {
		final IBattleAction[] br = battleResults[playerIndex];
		if (br == null)
			return;
		@SuppressWarnings("resource")
		final Session session = sessionStore.retrieve(players[playerIndex]);
		if (session != null)
			socketProcessing.dispatchMessage(session, ELunarStatusCode.OK, new MessageBattleStepped(br));
	}
}