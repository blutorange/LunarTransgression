package com.github.blutorange.translune.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.math.Fraction;
import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.common.CollectionUtil;
import com.github.blutorange.translune.db.Character;
import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.ModifiablePlayer;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;
import com.github.blutorange.translune.socket.BattleResult;
@Singleton
public class BattleProcessing implements IBattleProcessing {
	private static final BattleAction[] EMPTY_BATTLE_ACTION = new BattleAction[0];

	private static final Fraction TWO = Fraction.getFraction(2, 1);

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
		final Player[] playerEntities = databaseManager.findAll(Player.class, players);
		final IBattleContext battleContext = new BattleContext(getCharacterStates(characterStates),
				computedBattleStatus, items, getItemRemovable(playerEntities), battleStatus, effectorStack, players,
				battleActions1, battleActions2, turn);
		makeCommandHandler(battleContext, 0, battleCommandHandlers, 0, commands.get(0));
		makeCommandHandler(battleContext, 1, battleCommandHandlers, 4, commands.get(1));
		sortBattleAction(battleCommandHandlers);
		effectorBefore(battleContext, effectorStack);
		executeBattleActions(battleCommandHandlers);
		effectorAfter(battleContext, effectorStack);
		return new BattleAction[][] { battleActions1.toArray(EMPTY_BATTLE_ACTION),
				battleActions2.toArray(EMPTY_BATTLE_ACTION) };
	}

	@Override
	public BattleResult[][] distributeExperience(final String[] players, final List<String[]> characterStates,
			final BattleStatus[][] battleStatus, final int turn) {
		final IComputedBattleStatus[][] computedBattleStatus = computedBattleStatus(characterStates, battleStatus);
		final Player[] playerEntities = databaseManager.findAll(Player.class, players);
		// TODO [HIGH] compute experience, return exp gain and level up messages
		distributeExperience(playerEntities, computedBattleStatus, 0, turn);
		distributeExperience(playerEntities, computedBattleStatus, 1, turn);
		throw new NotImplementedException("implement me");
	}

	public void distributeExperience(final Player[] players, final IComputedBattleStatus[][] computedBattleStatus,
			final int player, final int turn) {
		final int opponent = 1-player;
		// Get total experience by summing the experience for each opponent
		int totalExp = 0;
		int knockoutCount  = 0;
		final Fraction partTurns = turn > 10 ? Fraction.ONE : TWO.subtract(Fraction.getFraction(turn, 10));
		for (int i = 4; i --> 0;) {
			final IComputedBattleStatus cbs = computedBattleStatus[opponent][i];
			final int level = cbs.getCharacterState().getLevel();
			final Fraction partLevel = Fraction.getFraction(level, 1).pow(3);
			final Fraction partHp = Fraction.ONE.subtract(cbs.getBattleStatus().getHpFraction()).pow(2)
					.add(Fraction.ONE_HALF).multiplyBy(TWO).reduce();
			if (cbs.getBattleStatus().getHp() <= 0)
				++knockoutCount;
			final int experience = partLevel.multiplyBy(partHp).multiplyBy(partTurns).intValue();
			totalExp += experience;
		}
		final Fraction winningFactor = knockoutCount >= 2 ? Fraction.getFraction(5,4) : Fraction.getFraction(3,4);
		totalExp = totalExp*winningFactor.getNumerator()/winningFactor.getDenominator();
		// Distribute experience the player's characters.
		int levelTotal = 0;
		for (int i = 4; i --> 0;)
			levelTotal += 101-computedBattleStatus[opponent][i].getCharacterState().getLevel();
		for (int i = 4; i --> 0;) {
			final int exp = 101 - computedBattleStatus[opponent][i].getCharacterState().getLevel() * totalExp / levelTotal;
			gainExp();
		}
	}
	
	private void gainExp(final CharacterState characterState, final int exp) {
		final Character character = characterState.getCharacter();
		final List<String> sentences = new ArrayList<>();
		final int oldLevel = characterState.getLevel();
		final int newLevel = character.getExperienceGroup().getLevelForExperience(characterState.getExp() + exp);
		sentences.add(String.format("%s gained %d XP.", characterState.getNickname(), exp));
		if (newLevel > oldLevel) {
			sentences.add(String.format("%s grew to level %d!", characterState.getNickname(), newLevel));
		}
		new BattleResult(character.getName(), sentences.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
	}

	private IItemRemovable getItemRemovable(final Player[] players) {
		return (player, item) -> databaseManager.modify(players[player], ModifiablePlayer.class,
				mp -> mp.removeItem(item));
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
		for (final Iterator<IGlobalBattleEffector> it = handler.getBattleContext().getEffectorStack().iterator(); it
				.hasNext();) {
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

	private void executeBattleActions(final IBattleCommandHandler[] battleCommandHandlers) {
		for (final IBattleCommandHandler battleCommandHandler : battleCommandHandlers) {
			battleCommandHandler.preProcess();
		}
		for (final IBattleCommandHandler battleCommandHandler : battleCommandHandlers) {
			if (effectorAllowTurn(battleCommandHandler)) {
				final int player = battleCommandHandler.getPlayerIndex();
				if (player == 0)
					battleCommandHandler.execute();
				else
					battleCommandHandler.execute();
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
	}

	private void processBattleEnd(final IBattleContext context, final int winningPlayer) {
		final BattleAction actionWinning = CollectionUtil.last(context.getBattleActions(winningPlayer));
		final BattleAction actionLosing = CollectionUtil.last(context.getBattleActionsOpponent(winningPlayer));
		if (actionWinning != null)
			actionWinning.setCausesEnd(1);
		if (actionLosing != null)
			actionLosing.setCausesEnd(-1);
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