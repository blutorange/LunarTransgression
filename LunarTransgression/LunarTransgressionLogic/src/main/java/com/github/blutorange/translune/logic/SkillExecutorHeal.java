package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;

public class SkillExecutorHeal extends ASkillExecutor {
	@Override
	public void execute(final Skill skill, final IBattleContext context, final BattleCommand battleCommand,
			final int player, final int character) {
		// Acquire user
		final IComputedBattleStatus user = context.getComputedBattleStatus(player, character);

		// Perform heal and inform combatants.
		final String useMessage = String.format("%s uses %s!", user.getCharacterState().getNickname(), skill.getName());
		final BattleAction.Builder builder = new BattleAction.Builder().character(user)
				.addSentences(useMessage);

		battleProcessing.performHeal(skill, user, builder.getSentences(), context);

		final BattleAction action = builder.build();
		context.getBattleActions(player).add(action);
		context.getBattleActionsOpponent(player).add(action);
	}
}