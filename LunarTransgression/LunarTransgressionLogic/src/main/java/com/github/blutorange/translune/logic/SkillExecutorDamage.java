package com.github.blutorange.translune.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;

public class SkillExecutorDamage extends ASkillExecutor {

	@Override
	public void execute(final Skill skill, final IBattleContext context, final BattleCommand battleCommand,
			final int player, final int character) {
		final IComputedBattleStatus[] targets = context.getTargetsAlive(skill, context, battleCommand, player, character);
		final IComputedBattleStatus user = context.getComputedBattleStatus(player, character);

		if (targets.length == 0) {
			handleError(context, user.getCharacterState(), skill, "But the target is gone!");
			return;
		}

		// Check MP.
		if (user.getComputedBattleMpAbsolute() < skill.getMp()) {
			handleError(context, user.getCharacterState(), skill, "But there are not enough MP left.");
			return;
		}

		// Reduce MP
		user.modifyMp(-skill.getMp());

		final String useMessage = skill.getIsPhysical() ? "%s unleashes %s!" : "%s casts %s!";

		// Have each target take damage.
		final IDamageResult[] damageResults = context.computeDamage(skill, user, targets);
		final List<String> messages = new ArrayList<>(2 * targets.length);
		messages.add(String.format(useMessage, user.getCharacterState().getNickname(), skill.getName()));
		for (int targetIndex = 0; targetIndex < targets.length; ++targetIndex) {
			// Have the target take damage.
			final CharacterState target = targets[targetIndex].getCharacterState();
			final IDamageResult damageResult = damageResults[targetIndex];
			final String effectiveMessage = damageResult.getTypeEffectiveness().getBattleMessage();
			targets[targetIndex].modifyHp(-damageResult.getDamage());
			// Inform players about the battle action.
			if (damageResult.isCritial())
				messages.add("Critical hit!");
			if (effectiveMessage != null)
				messages.add(effectiveMessage);
			messages.add(String.format("%s took %d damage!", target.getNickname(),
					Integer.valueOf(damageResult.getDamage())));
		}

		final BattleAction action = new BattleAction(user.getCharacterState().getId(),
				Arrays.stream(targets).map(t -> t.getCharacterState().getNickname()).toArray(String[]::new),
				messages.toArray(ArrayUtils.EMPTY_STRING_ARRAY));

		context.getBattleActions(player).add(action);
		context.getBattleActionsOpponent(player).add(action);
	}

	private void handleError(final IBattleContext context, final CharacterState user, final Skill skill,
			final String error) {
		super.handleError(context, user, String.format("%s casts %s!", user.getNickname(), skill.getName()), error);
	}
}