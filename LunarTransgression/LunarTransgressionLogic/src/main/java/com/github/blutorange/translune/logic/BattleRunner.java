package com.github.blutorange.translune.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.message.MessageBattleCancelled;
import com.github.blutorange.translune.message.MessageBattleEnded;
import com.github.blutorange.translune.message.MessageBattlePreparationCancelled;
import com.github.blutorange.translune.message.MessageBattlePrepared;
import com.github.blutorange.translune.message.MessageBattleStepped;
import com.github.blutorange.translune.serial.PlayerViewBattle;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;
import com.github.blutorange.translune.socket.BattleResult;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.util.CustomProperties;

public class BattleRunner implements IBattleRunner {
	private volatile boolean battleDone;

	private final List<String[]> characterStates;

	private final List<BattleCommand @Nullable []> commands;

	private final List<String[]> items;

	private final Phaser phaser;

	private final List<IGlobalBattleEffector> effectorStack;

	@SuppressWarnings("null")
	private final @NonNull String[] players = new String[] { StringUtils.EMPTY, StringUtils.EMPTY };

	private int round;

	private final BattleStatus[][] battleStatus = new BattleStatus[][] {
			new BattleStatus[] { new BattleStatus(), new BattleStatus(), new BattleStatus(), new BattleStatus() },
			new BattleStatus[] { new BattleStatus(), new BattleStatus(), new BattleStatus(), new BattleStatus() }, };

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
	public BattleRunner(final String from, final String to) {
		round = 0;
		characterStates = new CopyOnWriteArrayList<>(new String[2][]);
		items = new CopyOnWriteArrayList<>(new String[2][]);
		commands = Collections.synchronizedList(new ArrayList<>(Arrays.asList(null,null)));
		phaser = new Phaser(2);
		effectorStack = new CopyOnWriteArrayList<>();
		players[0] = from;
		players[1] = to;
		ComponentFactory.getLunarComponent().inject(this);
	}

	@Override
	public void preparePlayer(final String nickname, final String[] characterStates, final String[] items)
			throws IllegalStateException, IllegalArgumentException {
		synchronized (phaser) {
			if (phaser.isTerminated())
				throw new IllegalStateException("Not waiting for battle, phaser already terminated.");
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
			phaser.arrive();
		}
	}

	@Override
	public void battlePlayer(final String nickname, final BattleCommand character1, final BattleCommand character2,
			final BattleCommand character3, final BattleCommand character4)
			throws IllegalStateException, IllegalArgumentException {
		synchronized (phaser) {
			if (phaser.isTerminated())
				return;
			final int playerIndex = getPlayerIndex(nickname);
			if (commands.get(playerIndex) != null)
				throw new IllegalStateException("Player already finalized the current turn.");
			final BattleCommand[] cmds = new BattleCommand[] { character1, character2, character3, character4 };
			commands.set(playerIndex, cmds);
			phaser.arrive();
		}
	}

	@Override
	public void run() {
		synchronized (players) {
			if (players[0].isEmpty() || players[1].isEmpty())
				throw new IllegalStateException("Battle players are not set.");
		}
		try {
			while (!battleDone) {
				if (round == 0)
					battlePreparations();
				else
					battleStep(round);
				++round;
			}
		}
		catch (final Exception e) {
			logger.error("error occured during battle processing", e);
			if (!battleDone) {
				cancelBattle("Internal server error", round == 0);
				battleDone = true;
			}
		}
	}

	private void assertBattlePrep(final int playerIndex, final String[] characters, final String[] items) {
		Player player;
		player = databaseManager.find(Player.class, players[playerIndex]);
		if (player == null)
			throw new IllegalStateException("Player does not exist");
		for (final String character : characters)
			if (!player.getUnmodifiableCharacterStates()
					.contains(databaseManager.find(CharacterState.class, character)))
				throw new IllegalArgumentException("Player does not own this character: " + character);
		for (final String item : items)
			if (!player.getUnmodifiableItems().contains(databaseManager.find(Item.class, item)))
				throw new IllegalArgumentException("Player does not own this item: " + item);
	}

	private void battlePreparations() throws IOException {
		try {
			phaser.awaitAdvanceInterruptibly(phaser.getPhase(), customProperties.getBattlePreparationTimeoutMillis(),
					TimeUnit.MILLISECONDS);
		}
		catch (final InterruptedException e) {
			logger.error("battle preparations interrupted", e);
			cancelBattle("Internal server error", true);
			return;
		}
		catch (final TimeoutException e) {
			logger.info("battle preparations timed out", e);
		}
		if (battleDone)
			return;
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
			cancelBattle("The other player did not complete battle preparations in time.", true);
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

	private void battleStep(final int round) throws IOException {
		int timeout = customProperties.getBattleStepTimeoutMillis();
		if (round < 2)
			timeout += customProperties.getBattleStepFirstTimeoutMillis();
		try {
			phaser.awaitAdvanceInterruptibly(phaser.getPhase(), timeout, TimeUnit.MILLISECONDS);
		}
		catch (final InterruptedException e) {
			logger.error("battle step was interrupted", e);
			cancelBattle("Internal server error", false);
			return;
		}
		catch (final TimeoutException e) {
			logger.info("battle step timed out", e);
		}
		if (battleDone)
			return;
		boolean player1Done = true;
		boolean player2Done = true;
		synchronized (phaser) {
			if (battleDone)
				return;
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
			cancelBattle("The other player did enter battle commands in time.", true);
			return;
		}
		final int winner = processBattle();
		if (winner >= 0)
			processBattleEnd(winner);
	}

	private void processBattleEnd(final int winner) throws IOException {
		battleDone = true;
		final BattleResult[][] battleResults = battleProcessing.distributeExperience(players, characterStates,
				battleStatus, round);
		final String[] characterStatesWinner = characterStates.get(winner);
		final String[] itemsWinner = items.get(winner);
		if (characterStatesWinner == null || itemsWinner == null)
			throw new IOException("Winner items or characters not found.");
		battleStore.addLoot(players[winner], characterStatesWinner, itemsWinner);
		endBattle();
		if (winner == 0)
			setGameStateForBoth(EGameState.BATTLE_LOOT, EGameState.IN_BATTLE);
		else
			setGameStateForBoth(EGameState.IN_BATTLE, EGameState.BATTLE_LOOT);
		informPlayerAboutEnd(0, winner == 0, battleResults[0]);
		informPlayerAboutEnd(0, winner == 1, battleResults[1]);
	}

	private int processBattle() throws IOException {
		@SuppressWarnings("null")
		final List<@NonNull BattleCommand[]> c = (List<BattleCommand[]>) commands;
		final BattleAction[][] battleResults = battleProcessing.simulateBattleStep(c, players, characterStates, items,
				battleStatus, effectorStack, round);
		clearCommands();
		final int winner = battleProcessing.checkBattleEnd(battleStatus);
		informPlayerAboutStepped(0, winner, battleResults);
		informPlayerAboutStepped(1, winner, battleResults);
		return winner;
	}

	private void clearCommands() {
		synchronized (phaser) {
			commands.set(0, null);
			commands.set(1, null);
		}
	}

	private void endBattle() {
		synchronized (phaser) {
			battleDone = true;
			characterStates.clear();
			commands.set(0, null);
			commands.set(1, null);
			items.clear();
			// We already checked for null when battle processing began.
			battleStore.removeBattle(players[0], players[1]);
			phaser.forceTermination();
		}
	}

	private void cancelBattle(final String message, final boolean isPrep) {
		endBattle();
		setGameStateForBoth(EGameState.IN_MENU, EGameState.IN_MENU);
		informPlayerAboutCancel(0, message, isPrep);
		informPlayerAboutCancel(1, message, isPrep);
	}

	private int getPlayerIndex(final String nickname) {
		if (players[0].equals(nickname))
			return 0;
		if (players[1].equals(nickname))
			return 1;
		throw new IllegalArgumentException("Player does not participate in this battle: " + nickname);
	}

	@SuppressWarnings("resource")
	private void informPlayerAboutCancel(final int playerIndex, final String message, final boolean isPrep) {
		// We already checked when battle processing began.
		final Session session = sessionStore.retrieve(players[playerIndex]);
		if (session != null)
			socketProcessing.dispatchMessage(session, ELunarStatusCode.OK,
					isPrep ? new MessageBattlePreparationCancelled(message) : new MessageBattleCancelled(message));
	}

	private void informPlayerAboutEnd(final int playerIndex, final boolean isVictory,
			final BattleResult[] battleResults) {
		// We already checked when battle processing began.
		@SuppressWarnings("resource")
		final Session session = sessionStore.retrieve(players[playerIndex]);
		if (session != null)
			socketProcessing.dispatchMessage(session, ELunarStatusCode.OK,
					new MessageBattleEnded(isVictory, battleResults));
	}

	private void informPlayerAboutPrepared(final int playerIndex) throws IOException {
		// We already checked when battle processing began.
		@SuppressWarnings("resource")
		final Session session = sessionStore.retrieve(players[playerIndex]);
		if (session != null) {
			final IComputedBattleStatus[][] computed = battleProcessing.computedBattleStatus(characterStates, battleStatus);
			final Player p1 = databaseManager.find(Player.class, players[playerIndex]);
			final Player p2 = databaseManager.find(Player.class, players[1-playerIndex]);
			if (p1 == null || p2 == null)
				throw new IOException("Player not found: " + players[0] + ", " + players[1]);
			socketProcessing.dispatchMessage(session, ELunarStatusCode.OK, new MessageBattlePrepared(
					new PlayerViewBattle(p1, computed[playerIndex]),
					new PlayerViewBattle(p2, computed[1-playerIndex])
			));
		}
	}

	private void informPlayerAboutStepped(final int playerIndex, final int winner,
			final BattleAction[][] battleResults) {
		final int causesEnd = winner < 0 ? 0 : winner == playerIndex ? 1 : -1;
		final BattleAction[] br = battleResults[playerIndex];
		if (br == null)
			return;
		@SuppressWarnings("resource")
		final Session session = sessionStore.retrieve(players[playerIndex]);
		if (session != null)
			socketProcessing.dispatchMessage(session, ELunarStatusCode.OK, new MessageBattleStepped(br, causesEnd));
	}

	@Override
	public String[] getPlayers() {
		return this.players;
	}

	@Override
	public void cancel() {
		this.battleDone = true;
	}
}