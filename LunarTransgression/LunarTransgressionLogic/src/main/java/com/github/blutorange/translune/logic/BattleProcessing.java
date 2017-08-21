package com.github.blutorange.translune.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;

@Singleton
public class BattleProcessing implements IBattleProcessing {
	@Inject
	ILunarDatabaseManager databaseManager;

	@Inject
	public BattleProcessing() {

	}

	@Override
	public IComputedBattleStatus getComputedStatus(final CharacterState characterState,
			final BattleStatus battleStatus) {
		return new ComputedBattleStatus(characterState, battleStatus);
	}

	@Override
	public IComputedBattleStatus getComputedStatus(final String characterState, final BattleStatus status) {
		final CharacterState cs = databaseManager.find(CharacterState.class, characterState);
		if (cs == null)
			throw new IllegalArgumentException("character state does not exists");
		return getComputedStatus(cs, status);
	}

	@Override
	public BattleAction[][] simulateBattleStep(final List<BattleCommand[]> commands, final String[] players,
			final List<String[]> characterStates, final List<String[]> items, final BattleStatus[][] battleStatus,
			final List<IGlobalBattleEffector> effectorStack, final int turn) {
		final IComputedBattleStatus[][] computedBattleStatus = computedBattleStatus(characterStates, battleStatus);
		final IBattleCommandHandler[] battleCommandHandlers = new IBattleCommandHandler[8];
		final List<BattleAction> battleActions1 = new ArrayList<>(2 * battleCommandHandlers.length);
		final List<BattleAction> battleActions2 = new ArrayList<>(2 * battleCommandHandlers.length);
		final IBattleContext battleContext = new BattleContext(getCharacterStates(characterStates),
				computedBattleStatus, items, battleStatus, effectorStack, players, battleActions1, battleActions2, turn);
		makeCommandHandler(battleContext, 0, battleCommandHandlers, 0, commands.get(0));
		makeCommandHandler(battleContext, 1, battleCommandHandlers, 4, commands.get(1));
		sortBattleAction(battleCommandHandlers);
		effectorBefore(battleContext, effectorStack);
		final BattleAction[][] result = executeBattleActions(battleCommandHandlers, battleActions1, battleActions2);
		effectorAfter(battleContext, effectorStack);
		return result;
	}

	private void effectorAfter(final IBattleContext battleContext, final List<IGlobalBattleEffector> effectorStack) {
		for (final Iterator<IGlobalBattleEffector> it = effectorStack.iterator(); it.hasNext();) {
			final IGlobalBattleEffector effector = it.next();
			if (effector.afterTurn(battleContext)) {
				it.remove();
				effector.onRemove(battleContext);
			}
		}
	}

	private void effectorBefore(final IBattleContext battleContext, final List<IGlobalBattleEffector> effectorStack) {
		for (final Iterator<IGlobalBattleEffector> it = effectorStack.iterator(); it.hasNext();) {
			final IGlobalBattleEffector effector = it.next();
			if (effector.beforeTurn(battleContext)) {
				it.remove();
				effector.onRemove(battleContext);
			}
		}
	}

	private boolean effectorAllowTurn(final IBattleCommandHandler handler) {
		for (final Iterator<IGlobalBattleEffector> it = handler.getBattleContext().getEffectorStack().iterator(); it.hasNext();) {
			final IGlobalBattleEffector effector = it.next();
			if (!effector.allowTurn(handler.getBattleContext(), handler.getCharacterState()))
				return false;
		}
		return true;
	}

	private CharacterState[][] getCharacterStates(final List<String[]> characterStates) {
		final CharacterState[][] retrievedCharacterStates = new CharacterState[2][4];
		for (int player = 1; player-- > 0;) {
			for (int character = 1; character-- > 0;) {
				final CharacterState characterState = databaseManager.find(CharacterState.class,
						characterStates.get(player)[character]);
				if (characterState == null)
					throw new IllegalStateException(
							"character does not exist: " + characterStates.get(player)[character]);
				retrievedCharacterStates[player][character] = characterState;
			}
		}
		return retrievedCharacterStates;
	}

	private BattleAction[][] executeBattleActions(final IBattleCommandHandler[] battleCommandHandlers,
			final List<BattleAction> battleActions1, final List<BattleAction> battleActions2) {
		for (final IBattleCommandHandler battleCommandHandler : battleCommandHandlers) {
			battleCommandHandler.preProcess();
		}
		for (final IBattleCommandHandler battleCommandHandler : battleCommandHandlers) {
			if (effectorAllowTurn(battleCommandHandler)) {
				final int player = battleCommandHandler.getPlayerIndex();
				if (player == 0)
					battleCommandHandler.execute(battleActions1, battleActions2);
				else
					battleCommandHandler.execute(battleActions2, battleActions1);
			}
			final int winningPlayer = checkBattleEnd(battleCommandHandler.getBattleContext().getBattleStatus());
			if (winningPlayer >= 0) {
				processBattleEnd(battleCommandHandler.getBattleContext(), winningPlayer);
				break;
			}
		}
		for (final IBattleCommandHandler battleCommandHandler : battleCommandHandlers) {
			battleCommandHandler.postProcess();
		}
		return new BattleAction[][] { battleActions1.toArray(new BattleAction[0]),
				battleActions2.toArray(new BattleAction[0]) };
	}

	private void processBattleEnd(final IBattleContext battleContext, final int winningPlayer) {
		// TODO Process battle win. Add message with isWin=true, think about EXP gain? Send message?
		throw new RuntimeException("TODO - not yet implemented");
	}

	private int getPlayerIndex(final String[] players, final IBattleCommandHandler battleCommandHandler) {
		final String user = battleCommandHandler.getUser();
		if (players[0].equals(user))
			return 0;
		return 1;
	}

	private void makeCommandHandler(final IBattleContext battleContext, final int player,
			final IBattleCommandHandler[] out, final int offset, final BattleCommand... battleCommands) {
		for (int character = 4; character-- > 0;) {
			final CharacterState characterState = battleContext.getCharacterState(player, character);
			if (characterState == null)
				throw new IllegalArgumentException("Character state does not exist: " + player + ", " + character);
			out[character + offset] = IBattleCommandHandler.create(battleContext, player, character,
					battleCommands[character]);
		}
	}

	private IComputedBattleStatus[][] computedBattleStatus(final List<String @NonNull []> characterStates,
			final BattleStatus[][] battleStatus) {
		final IComputedBattleStatus[][] computedBattleStatus = new IComputedBattleStatus[2][4];
		for (int player = 1; player-- > 0;) {
			for (int character = 4; character-- > 0;) {
				computedBattleStatus[player][character] = getComputedStatus(characterStates.get(0)[0],
						battleStatus[0][0]);
			}
		}
		return computedBattleStatus;
	}

	@Override
	public int checkBattleEnd(final BattleStatus[][] battleStatus) {
		if (getKnockoutCount(battleStatus[0]) >= 2)
			return 0;
		if (getKnockoutCount(battleStatus[1]) >= 2)
			return 1;
		return -1;
	}

	private int getKnockoutCount(final BattleStatus[] battleStatus) {
		int count = 0;
		for (final BattleStatus bs : battleStatus) {
			if (bs.getHp() <= 0)
				++count;
		}
		return count;
	}

	private void sortBattleAction(final IBattleCommandHandler[] commands) {
		Arrays.sort(commands, (command1, command2) -> {
			final int res1 = Integer.compare(command1.getPriority(), command2.getPriority());
			if (res1 != 0)
				return res1;
			return Integer.compare(command1.getSpeed(), command2.getSpeed());
		});
	}
}