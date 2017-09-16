package com.github.blutorange.translune.logic;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;

public abstract class ASkillExecutor<T> extends AExecutor implements ISkillExecutor {
	protected void basic(final String useMessage, final Skill skill, final IBattleContext context,
			final BattleCommand battleCommand, final int player, final int character, final T object) {
		// Acquire user and targets.
		final IComputedBattleStatus[] targets = battleProcessing.getTargetsAlive(skill, context, battleCommand, player,
				character);
		final IComputedBattleStatus user = context.getComputedBattleStatus(player, character);

		final String formattedUseMessage = String.format(useMessage, user.getCharacterState().getNickname(),
				skill.getName());

		if (targets.length == 0) {
			handleError(context, user.getCharacterState(), formattedUseMessage, "But the target is gone!");
			return;
		}

		// Check MP.
		if (user.getComputedBattleMpAbsolute() < skill.getMp()) {
			handleError(context, user.getCharacterState(), formattedUseMessage, "But there are not enough MP left.");
			return;
		}

		// Reduce MP
		user.modifyMp(-skill.getMp());

		final List<String> messages = new ArrayList<>();
		messages.add(formattedUseMessage);

		final IDamageResult @Nullable [] damageResults;
		if (skill.getAttackPower() > 0)
			damageResults = battleProcessing.computeDamage(skill, user, targets);
		else
			damageResults = damageResultOnNoAttackPower(skill, user, targets);

		for (int i = 0; i < targets.length; ++i) {
			// Misses
			if (!battleProcessing.moveHits(skill, user, targets[i], messages))
				continue;

			int damage = targets[i].getComputedBattleHpAbsolute();

			// Compute and deal damage
			if (damageResults != null)
				battleProcessing.dealDamage(damageResults[i], targets[i], messages);

			damage -= targets[i].getComputedBattleHpAbsolute();

			afterDamage(user, targets[i], damage, messages, context, object);

			// Inflict status conditions
			battleProcessing.inflictCondition(skill, targets[i], messages, context);

			// Stage changes
			battleProcessing.changeStages(skill, targets[i], messages, context);

			// Flinch
			battleProcessing.makeFlinch(skill, targets[i], messages, context);
		}

		// Add result
		final BattleAction action = new BattleAction.Builder(context).character(user).targets(targets).addSentences(messages)
				.build();
		context.getBattleActions(player).add(action);
		context.getBattleActionsOpponent(player).add(action);
	}

	protected IDamageResult @Nullable [] damageResultOnNoAttackPower(final Skill skill, final IComputedBattleStatus user,
			final IComputedBattleStatus[] targets) {
		return null;
	}

	/**
	 * @param target
	 * @param damage
	 * @param messages
	 */
	protected void afterDamage(final IComputedBattleStatus user, final IComputedBattleStatus target, final int damage,
			final List<String> messages, final IBattleContext context, final T object) {
		// May be overridden.
	}

	protected void handleError(final IBattleContext context, final CharacterState user, final String useMessage,
			final String error) {
		battleProcessing.handleError(context, user, useMessage, error);
	}
}