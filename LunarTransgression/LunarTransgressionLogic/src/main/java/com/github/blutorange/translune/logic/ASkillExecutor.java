package com.github.blutorange.translune.logic;

import java.util.ArrayList;
import java.util.List;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;

public abstract class ASkillExecutor extends AExecutor implements ISkillExecutor {
	protected void basic(final String useMessage, final Skill skill, final IBattleContext context,
			final BattleCommand battleCommand, final int player, final int character) {
		// Acquire user and targets.
		final IComputedBattleStatus[] targets = battleProcessing.getTargetsAlive(skill, context, battleCommand, player,
				character);
		final IComputedBattleStatus user = context.getComputedBattleStatus(player, character);

		if (targets.length == 0) {
			handleError(context, user.getCharacterState(), skill, useMessage, "But the target is gone!");
			return;
		}

		// Check MP.
		if (user.getComputedBattleMpAbsolute() < skill.getMp()) {
			handleError(context, user.getCharacterState(), skill, useMessage, "But there are not enough MP left.");
			return;
		}

		// Reduce MP
		user.modifyMp(-skill.getMp());

		final List<String> messages = new ArrayList<>();
		messages.add(useMessage);

		final IDamageResult[] damageResults;
		if (skill.getAttackPower() > 0)
			damageResults = battleProcessing.computeDamage(skill, user, targets);
		else
			damageResults = new IDamageResult[targets.length];

		for (int i = 0; i < targets.length; ++i) {
			// Misses
			if (!battleProcessing.moveHits(skill, user, targets[i], messages))
				continue;

			// Compute and deal damage
			if (skill.getAttackPower() > 0)
				battleProcessing.dealDamage(damageResults[i], targets[i], messages);

			// Inflict status conditions
			battleProcessing.inflictCondition(skill, targets[i], messages, context);

			// Stage changes
			battleProcessing.changeStages(skill, targets[i], messages, context);

			// Flinch
			battleProcessing.makeFlinch(skill, targets[i], messages, context);
		}

		// Add result
		final BattleAction action = new BattleAction.Builder().character(user).targets(targets).addSentences(messages)
				.build();
		context.getBattleActions(player).add(action);
		context.getBattleActionsOpponent(player).add(action);
	}


	protected void handleError(final IBattleContext context, final CharacterState user, final Skill skill,
			final String useMessage, final String error) {
		battleProcessing.handleError(context, user, useMessage, error);
	}
}