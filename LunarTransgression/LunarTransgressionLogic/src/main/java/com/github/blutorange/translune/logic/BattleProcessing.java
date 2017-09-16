package com.github.blutorange.translune.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.Fraction;
import org.apache.commons.math3.fraction.BigFraction;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.common.CollectionUtil;
import com.github.blutorange.common.ComparatorUtil;
import com.github.blutorange.translune.db.Character;
import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.EStatusValue;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.ModifiableCharacterState;
import com.github.blutorange.translune.db.ModifiablePlayer;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.logic.BattleContext.DamageResult;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;
import com.github.blutorange.translune.socket.BattleResult;
import com.github.blutorange.translune.util.Constants;

@Singleton
public class BattleProcessing implements IBattleProcessing {
	private static final BattleAction[] EMPTY_BATTLE_ACTION = new BattleAction[0];

	@Inject
	ILunarDatabaseManager databaseManager;

	@Inject
	@Named("basic")
	IRandomSupplier random;

	@Inject
	public BattleProcessing() {

	}

	@Override
	public int checkBattleEnd(final IBattleStatus[][] battleStatus) {
		if (getKnockoutCount(battleStatus[0]) >= 2)
			return 1;
		if (getKnockoutCount(battleStatus[1]) >= 2)
			return 0;
		return -1;
	}

	@Override
	public IDamageResult[] computeDamage(final ISkilled skill, final IComputedBattleStatus attacker,
			final IComputedBattleStatus... defenders) {
		BigFraction modifier = BigFraction.ONE;
		boolean isStab = false;

		// If the move has more than one target,
		if (defenders.length > 1)
			modifier = modifier.multiply(BigFraction.THREE_QUARTERS);

		// Same-type attack bonus. This is equal to 1.5 if the move's type
		// matches any
		// of the user's types, and 1 if otherwise.
		if (attacker.getCharacterState().getCharacter().getUnmodifiableElements().contains(skill.getElement())) {
			modifier = modifier.multiply(Constants.FRACTION_THREE_HALFS);
			isStab = true;
		}

		// Halved if the attacker is burned and the used move is a physical move
		if (skill.getIsPhysical() && EStatusCondition.BURN.equals(attacker.getBattleStatus().getStatusCondition()))
			modifier.multiply(BigFraction.ONE_HALF);

		// Compute damage for each target
		final IDamageResult[] results = new IDamageResult[defenders.length];
		for (int i = 0; i < defenders.length; ++i) {
			final IComputedBattleStatus defender = defenders[i];
			final DamageResult result = new DamageResult();
			result.setStab(isStab);

			BigFraction modifierSeparate = modifier;

			// 1.5 for a critical hit
			if (random.get().nextInt(100) < getCriticalHitThreshold(skill, attacker, defender)) {
				modifierSeparate = modifierSeparate.multiply(Constants.FRACTION_THREE_HALFS);
				result.setCritical(true);
			}

			// Type effectiveness. This can be 0 (ineffective),
			// 0.25, 0.5 (not very effective), 1 (normally effective),
			// 2 or 4 (super effective) depending on both the move's
			// and target's types.
			final ETypeEffectiveness typeEffectiveness = computeTypeEffectiveness(skill, defender.getCharacterState());
			final BigFraction typeMultiplier = typeEffectiveness.getMultiplier();
			modifierSeparate = modifierSeparate.multiply(typeMultiplier);
			result.setTypeEffectivness(typeEffectiveness);

			// Random factor between 0.85 and 1.00
			modifierSeparate = modifierSeparate
					.multiply(BigFraction.getReducedFraction(random.get().nextInt(16) + 85, 100));

			// Attack and defense
			final int attack = skill.getIsPhysical() ? attacker.getComputedBattlePhysicalAttack()
					: attacker.getComputedBattleMagicalAttack();
			final int defense = skill.getIsPhysical() ? defender.getComputedBattlePhysicalDefense()
					: defender.getComputedBattleMagicalDefense();

			// Damage calculation
			final int damage;
			if (typeMultiplier.equals(BigFraction.ZERO))
				damage = 0;
			else
				damage = (((2 * attacker.getCharacterState().getLevel()) / 5 + 2) * skill.getAttackPower() * attack
						/ defense) / 50 + 2 * modifierSeparate.intValue();

			result.setDamage(damage);
			results[i] = result;
		}
		return results;
	}

	@Override
	public boolean moveHits(final IAccuracied accuracyData, final IComputedBattleStatus user,
			final IComputedBattleStatus target, final List<String> messages) {
		final int userAccuracy = user.getComputedBattleAccuracy();
		final int opponentEvasion = target.getComputedBattleEvasion();
		final boolean hit = accuracyData.getAlwaysHits()
				|| random.get().nextInt(100) * opponentEvasion < accuracyData.getAccuracy() * userAccuracy;
		if (!hit)
			messages.add(String.format("But %s evaded the attack", target.getCharacterState().getNickname()));
		return hit;
	}

	@Override
	public void performHeal(final IHealing healData, final IComputedBattleStatus user, final List<String> messages,
			final IBattleContext context) {
		// Computed heal amount and do the healing.
		final int hpBefore = user.getComputedBattleHpAbsolute();
		final int healAmount = healData.getHealPower() * user.getComputedBattleMaxHp() / 100;
		user.modifyHp(healAmount);
		final int hpAfter = user.getComputedBattleHpAbsolute();

		// Inform the combatants.
		if (hpAfter != hpBefore)
			messages.add(String.format("%s healed its HP by %d!", Integer.valueOf(hpAfter - hpBefore)));
		else
			messages.add("But its HP were already full.");
	}

	@Override
	public void makeFlinch(final IFlinched flinchData, final IComputedBattleStatus target, final List<String> messages,
			final IBattleContext context) {
		final int flinchChance = flinchData.getFlinchChance();
		if (flinchChance < 1)
			return;
		if (random.get().nextInt(100) >= flinchChance)
			return;
		context.pushBattleEffector(new EffectorFlinch(context.getCharacterIndex(target)));
		messages.add(String.format("%s flinched.", target.getCharacterState().getNickname()));
	}

	@Override
	public void inflictCondition(final IStatusConditioned conditionData, final IComputedBattleStatus target,
			final List<String> messages, final IBattleContext context) {
		final EStatusCondition condition = conditionData.getCondition();
		if (condition == null)
			return;
		if (target.getBattleStatus().getStatusCondition() != null)
			return;
		if (random.get().nextInt(100) >= conditionData.getConditionChance())
			return;
		final int[] index = context.getCharacterIndex(target.getCharacterState());
		context.pushBattleEffector(new EffectorStatusCondition(index[0], index[1], condition));
		messages.add(String.format(condition.getInflictMessage(), target.getCharacterState().getNickname()));
	}

	@Override
	public void changeStages(final IStaged stageData, final IComputedBattleStatus target, final List<String> messages,
			final IBattleContext context) {
		if (random.get().nextInt(100) >= stageData.getStageChance())
			return;
		final int[] index = context.getCharacterIndex(target);
		stageData.getStageChanges().entrySet().forEach(entry -> {
			final EStatusValue statusValue = entry.getKey();
			final int amount = entry.getValue().intValue();
			if (amount == 0)
				return;
			context.pushBattleEffector(new EffectorTurnTimed(Constants.BATTLE_STAGE_CHANGE_TURNS,
					new EffectorBattleStatus(bs -> statusValue.changeStage(bs, amount), index[0], index[1],
							bs -> statusValue.changeStage(bs, -amount))));
			final String message = amount > 0 ? "%s's %s went up by %d stages." : "%s's %s fell by %d stages.";
			messages.add(String.format(message, target.getCharacterState().getNickname(),
					statusValue.name().toLowerCase(Locale.ROOT), Integer.valueOf(Math.abs(amount))));
		});
	}

	@Override
	public void dealDamage(final IDamageResult damageResult, final IComputedBattleStatus target,
			final List<String> messages) {
		// When no damage was done, we do not show a separate message.
		// Have the target take damage.
		final String effectiveMessage = damageResult.getTypeEffectiveness().getBattleMessage();
		target.modifyHp(-damageResult.getDamage());
		// Inform players about the battle action.
		if (damageResult.isCritial())
			messages.add("Critical hit!");
		if (effectiveMessage != null)
			messages.add(effectiveMessage);
		if (damageResult.getDamage() == 0)
			return;
		messages.add(String.format("%s took %d damage!", target.getCharacterState().getNickname(),
				Integer.valueOf(damageResult.getDamage())));
		if (target.getBattleStatus().getHp() <= 0) {
			messages.add(String.format("%s fainted!", target.getCharacterState().getNickname()));
		}
	}

	@Override
	public BattleResult[][] distributeExperience(final String[] players, final List<String[]> characterStates,
			final IBattleStatus[][] battleStatus, final int turn) throws IOException {
		final IComputedBattleStatus[][] computedBattleStatus = computedBattleStatus(characterStates, battleStatus);
		final Player[] playerEntities = databaseManager.findAll(Player.class, players);
		final BattleResult[][] result = new BattleResult[2][];
		result[0] = distributeExperience(playerEntities, computedBattleStatus, 0, turn);
		result[1] = distributeExperience(playerEntities, computedBattleStatus, 1, turn);
		return result;
	}

	@Override
	public IComputedBattleStatus getComputedStatus(final CharacterState characterState,
			final IBattleStatus battleStatus) {
		return IComputedBattleStatus.get(characterState, battleStatus);
	}

	@Override
	public IComputedBattleStatus getComputedStatus(final String characterState, final IBattleStatus status)
			throws IOException {
		final CharacterState cs = databaseManager.find(CharacterState.class, characterState);
		if (cs == null)
			throw new IllegalArgumentException("character state does not exists");
		return getComputedStatus(cs, status);
	}

	@Override
	public IComputedBattleStatus[] getTargetsAlive(final ITargettable targettable, final IBattleContext context,
			final BattleCommand battleCommand, final int player, final int character) {
		final IComputedBattleStatus[] targets = targettable.getTarget().getTargets(context, battleCommand, player,
				character);
		return Arrays.stream(targets).filter(target -> target.getBattleStatus().getHp() > 0)
				.toArray(IComputedBattleStatus[]::new);
	}

	@Override
	public void handleError(final IBattleContext context, final CharacterState user, final String... messages) {
		final BattleAction action = new BattleAction.Builder(context).character(user).targets(user)
				.addSentences(messages).build();
		final int player = context.getCharacterIndex(user)[0];
		context.getBattleActions(player).add(action);
		context.getBattleActionsOpponent(player).add(action);
	}

	@Override
	public BattleAction[][] simulateBattleStep(final List<BattleCommand[]> commands, final String[] players,
			final List<String[]> characterStates, final List<String[]> items, final BattleStatus[][] battleStatus,
			final List<IGlobalBattleEffector> effectorStack, final IComputedBattleStatus[][] computedBattleStatus,
			final int turn) throws IOException {
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
	public IComputedBattleStatus[][] computedBattleStatus(final List<String @NonNull []> characterStates,
			final IBattleStatus[][] battleStatus) throws IOException {
		final IComputedBattleStatus[][] computedBattleStatus = new IComputedBattleStatus[2][4];
		for (int player = 2; player-- > 0;) {
			for (int character = 4; character-- > 0;) {
				final String[] states = characterStates.get(player);
				if (states == null)
					throw new IOException("character states are null: " + player + "," + character);
				computedBattleStatus[player][character] = getComputedStatus(states[character],
						battleStatus[player][character]);
			}
		}
		return computedBattleStatus;
	}

	private ETypeEffectiveness computeTypeEffectiveness(final ISkilled skill, final CharacterState characterState) {
		final Set<EElement> elements = characterState.getCharacter().getUnmodifiableElements();
		final EElement element = skill.getElement();
		final BigFraction multiplier = elements.stream()
				.collect(Collectors.reducing(BigFraction.ONE, e -> e.typeEffectivness(element), BigFraction::multiply))
				.reduce();
		if (multiplier.equals(Fraction.ONE))
			return ETypeEffectiveness.NORMALLY_EFFECTIVE;
		if (multiplier.equals(Fraction.ONE_HALF))
			return ETypeEffectiveness.NOT_VERY_EFFECTIVE;
		if (multiplier.equals(Constants.FRACTION_TWO))
			return ETypeEffectiveness.SUPER_EFFECTIVE;
		if (multiplier.equals(Fraction.ZERO))
			return ETypeEffectiveness.INEFFECTIVE;
		return ETypeEffectiveness.HYPER_EFFECTIVE;
	}

	private BattleResult[] distributeExperience(final Player[] players,
			final IComputedBattleStatus[][] computedBattleStatus, final int player, final int turn) {
		final int opponent = 1 - player;
		// Get total experience by summing the experience for each opponent
		int totalExp = 0;
		int knockoutCount = 0;
		final BigFraction partTurns = turn > 10 ? BigFraction.ONE
				: Constants.FRACTION_TWO.subtract(BigFraction.getReducedFraction(turn, 10));
		for (int i = 4; i-- > 0;) {
			final IComputedBattleStatus cbs = computedBattleStatus[opponent][i];
			final int level = cbs.getCharacterState().getLevel();
			final BigFraction partLevel = BigFraction.getReducedFraction(level, 1).pow(3);
			final BigFraction partHp = BigFraction.ONE.subtract(cbs.getBattleStatus().getHpFraction()).pow(2)
					.add(BigFraction.ONE_HALF).multiply(Constants.FRACTION_TWO).reduce();
			if (cbs.getBattleStatus().getHp() <= 0)
				++knockoutCount;
			final int experience = partLevel.multiply(partHp).multiply(partTurns).intValue();
			totalExp += experience;
		}
		final Fraction winningFactor = knockoutCount >= 2 ? Fraction.getFraction(5, 4) : Fraction.getFraction(3, 4);
		totalExp = totalExp * winningFactor.getNumerator() / winningFactor.getDenominator();
		// Distribute experience the player's characters.
		int levelTotal = 0;
		for (int i = 4; i-- > 0;)
			levelTotal += 101 - computedBattleStatus[opponent][i].getCharacterState().getLevel();
		// Level up characters, show info regarding new skills etc.
		final BattleResult[] result = new BattleResult[4];
		for (int i = 4; i-- > 0;) {
			final int exp = (101
					- computedBattleStatus[opponent][i].getCharacterState().getLevel()) * totalExp / levelTotal;
			result[i] = gainExp(computedBattleStatus[opponent][i], exp);
		}
		return result;
	}

	private void effectorAfter(final IBattleContext battleContext, final List<IGlobalBattleEffector> effectorStack) {
		effectorStack.removeIf(effector -> {
			if (effector.afterTurn(battleContext)) {
				effector.onRemove(battleContext);
				return true;
			}
			return false;
		});
	}

	private boolean effectorAllowTurn(final IBattleCommandHandler handler) {
		for (final Iterator<IGlobalBattleEffector> it = handler.getBattleContext().getEffectorStack().iterator(); it
				.hasNext();) {
			final IGlobalBattleEffector effector = it.next();
			if (!effector.allowTurn(handler.getBattleContext(), handler.getComputedBattleStatus()))
				return false;
		}
		return true;
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

	private void executeBattleActions(final IBattleCommandHandler[] battleCommandHandlers) {
		for (final IBattleCommandHandler battleCommandHandler : battleCommandHandlers) {
			battleCommandHandler.preProcess();
		}
		for (final IBattleCommandHandler battleCommandHandler : battleCommandHandlers) {
			if (effectorAllowTurn(battleCommandHandler)) {
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

	private BattleResult gainExp(final IComputedBattleStatus status, final int exp) {
		final CharacterState characterState = status.getCharacterState();
		final IComputedStatus oldStatus = status.copy();
		final Character character = status.getCharacterState().getCharacter();
		final List<String> sentences = new ArrayList<>();
		final int oldLevel = characterState.getLevel();
		final int newExp = characterState.getExp() + exp;
		final int newLevel = character.getExperienceGroup().getLevelForExperience(newExp);
		sentences.add(String.format("%s gained %d XP.", characterState.getNickname(), exp));
		// Modify level +exp
		databaseManager.modify(characterState, ModifiableCharacterState.class, modifiable -> {
			modifiable.setLevel(newLevel);
			modifiable.setExp(newExp);
		});
		// Check which stats went up as a result.
		if (newLevel > oldLevel) {
			sentences.add(String.format("%s grew to level %d!", characterState.getNickname(), newLevel));
			character.getUnmodifiableSkills().entrySet().stream()
					.filter(entry -> entry.getValue() > oldLevel && entry.getValue() <= newLevel)
					.sorted(ComparatorUtil.byMapper(Entry::getValue)).map(Entry::getKey)
					.map(skill -> String.format("%s learned %s!", characterState.getNickname(), skill.getName()))
					.forEachOrdered(sentence -> sentences.add(sentence));
			sentences.add(String.format("HP went up by %d.", status.getComputedMaxHp() - oldStatus.getComputedMaxHp()));
			sentences.add(String.format("MP went up by %d.", status.getComputedMaxMp() - oldStatus.getComputedMaxMp()));
			sentences.add(String.format("Attack went up by %d.",
					status.getComputedPhysicalAttack() - oldStatus.getComputedPhysicalAttack()));
			sentences.add(String.format("Defense went up by %d.",
					status.getComputedPhysicalDefense() - oldStatus.getComputedPhysicalDefense()));
			sentences.add(String.format("Special attack went up by %d.",
					status.getComputedMagicalAttack() - oldStatus.getComputedMagicalAttack()));
			sentences.add(String.format("Special defense went up by %d.",
					status.getComputedMagicalDefense() - oldStatus.getComputedMagicalDefense()));
			sentences.add(
					String.format("Speed went up by %d.", status.getComputedSpeed() - oldStatus.getComputedSpeed()));
		}
		return new BattleResult(character.getName(), sentences.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
	}

	private CharacterState[][] getCharacterStates(final List<String[]> characterStates) throws IOException {
		final CharacterState[][] retrievedCharacterStates = new CharacterState[2][4];
		for (int player = 2; player-- > 0;) {
			for (int character = 4; character-- > 0;) {
				final String[] states = characterStates.get(player);
				if (states == null)
					throw new IOException("character states are null: " + player + "," + character);
				final CharacterState characterState = databaseManager.find(CharacterState.class, states[character]);
				if (characterState == null)
					throw new IllegalStateException("character does not exist: " + player + "," + character);
				retrievedCharacterStates[player][character] = characterState;
			}
		}
		return retrievedCharacterStates;
	}

	private int getCriticalHitThreshold(final ISkilled skill, final IComputedBattleStatus attacker,
			final IComputedBattleStatus defender) {
		int threshold = 100 * attacker.getComputedBattleSpeed() / 512;
		if (skill.getHighCritical())
			threshold *= 8;
		return threshold;
	}

	private IItemRemovable getItemRemovable(final Player[] players) {
		return (player, item) -> databaseManager.modify(players[player], ModifiablePlayer.class,
				mp -> mp.removeItem(item));
	}

	private int getKnockoutCount(final IBattleStatus[] battleStatus) {
		int count = 0;
		for (final IBattleStatus bs : battleStatus) {
			if (bs.getHp() <= 0)
				++count;
		}
		return count;
	}

	private void makeCommandHandler(final IBattleContext context, final int player, final IBattleCommandHandler[] out,
			final int offset, final BattleCommand @Nullable... battleCommands) throws IOException {
		if (battleCommands == null)
			throw new IOException("battle commands are null");
		for (int character = 4; character-- > 0;) {
			final CharacterState characterState = context.getCharacterState(player, character);
			if (characterState == null)
				throw new IllegalArgumentException("Character state does not exist: " + player + ", " + character);
			out[character + offset] = IBattleCommandHandler.create(context, player, character,
					battleCommands[character]);
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

	private void sortBattleAction(final IBattleCommandHandler[] commands) {
		Arrays.sort(commands, (command1, command2) -> {
			final int res = -Integer.compare(command1.getPriority(), command2.getPriority());
			if (res != 0)
				return res;
			return -Integer.compare(command1.getSpeed(), command2.getSpeed());
		});
	}
}