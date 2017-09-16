package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;

public class SkillExecutorNoEffect implements ISkillExecutor {
	@Override
	public void execute(final Skill skill, final IBattleContext context, final BattleCommand battleCommand,
			final int player, final int character) {
		final CharacterState user = context.getCharacterState(player, character);
		final BattleAction action = new BattleAction.Builder(context).character(user).addSentences(
				String.format("%s used %s.", user.getNickname(), skill.getName()), "But nothing happended.").build();
		context.getBattleActions(player).add(action);
		context.getBattleActionsOpponent(player).add(action);
	}
}