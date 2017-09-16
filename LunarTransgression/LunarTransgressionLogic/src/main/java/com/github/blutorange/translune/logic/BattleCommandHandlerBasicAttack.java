package com.github.blutorange.translune.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.EStatusValue;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;
import com.github.blutorange.translune.util.Constants;

public class BattleCommandHandlerBasicAttack extends ABattleCommandHandler {

	private static class BasicAttack implements ISkilled, IAccuracied, IStatusConditioned, IFlinched, IStaged {

		private EElement element;

		public BasicAttack(final IComputedBattleStatus status) {
			final Set<EElement> elements = status.getCharacterState().getCharacter().getUnmodifiableElements();
			if (elements.isEmpty()) {
				element = EElement.NORMAL;
			}
			element = elements.iterator().next();
		}

		@Override
		public EElement getElement() {
			return element;
		}

		@Override
		public boolean getHighCritical() {
			return false;
		}

		@Override
		public boolean getIsPhysical() {
			return element.isPhysical();
		}

		@Override
		public int getAttackPower() {
			return Constants.BATTLE_BASIC_ATTACK_POWER;
		}

		@Override
		@Nullable
		public EStatusCondition getCondition() {
			return EStatusCondition.PARALYSIS;
		}

		@Override
		public int getConditionChance() {
			return Constants.BATTLE_BASIC_ATTACK_CONDITION_CHANCE;
		}

		@Override
		public int getAccuracy() {
			return 0;
		}

		@Override
		public boolean getAlwaysHits() {
			return true;
		}

		@Override
		public int getFlinchChance() {
			return Constants.BATTLE_BASIC_ATTACK_FLINCH_CHANCE;
		}

		@Override
		public Map<EStatusValue, Integer> getStageChanges() {
			return Collections.emptyMap();
		}

		@Override
		public int getStageChance() {
			return 0;
		}
	}

	public BattleCommandHandlerBasicAttack(final IBattleContext battleContext, final int player, final int character,
			final BattleCommand battleCommand) {
		super(battleContext, player, character, battleCommand);
	}

	@Override
	public void execute() {
		// Acquire user and targets.
		final IComputedBattleStatus[] targets = battleProcessing.getTargetsAlive(() -> EActionTarget.OPPONENTS_FIELD, context,
				battleCommand, player, character);
		final IComputedBattleStatus user = context.getComputedBattleStatus(player, character);

		if (targets.length != 1) {
			handleError(context, user.getCharacterState(), "But the target is gone.");
			return;
		}

		final List<String> messages = new ArrayList<>();
		final BasicAttack basicAttack = new BasicAttack(user);
		final String useMessage = String.format("%s attacks %s!", user.getCharacterState().getNickname(), targets[0].getCharacterState().getNickname());
		messages.add(useMessage);

		// Check if attack hits.
		if (battleProcessing.moveHits(basicAttack, user, targets[0], messages)) {
			// Compute and deal damage
			final IDamageResult[] damageResults = battleProcessing.computeDamage(basicAttack, user, targets);
			battleProcessing.dealDamage(damageResults[0], targets[0], messages);
			// Stage changes
			battleProcessing.changeStages(basicAttack, targets[0], messages, context);
			// Flinch
			battleProcessing.makeFlinch(basicAttack, targets[0], messages, context);
		}

		// Add result
		final BattleAction action = new BattleAction.Builder(context).character(user).targets(targets[0]).addSentences(messages).build();
		context.getBattleActions(player).add(action);
		context.getBattleActionsOpponent(player).add(action);
	}

	@Override
	public int getPriority() {
		return Constants.BATTLE_PRIORITY_BASIC_ATTACK;
	}

	@Override
	public void postProcess() {
		// not needed
	}

	@Override
	public void preProcess() {
		// not needed
	}

	private void handleError(final IBattleContext context, final CharacterState user, final String error) {
		battleProcessing.handleError(context, user,
				new String[] { String.format("%s attacks!", user.getNickname()), error });
	}
}