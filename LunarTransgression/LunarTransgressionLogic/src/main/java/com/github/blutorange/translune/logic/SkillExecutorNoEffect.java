package com.github.blutorange.translune.logic;

import org.apache.commons.lang3.ArrayUtils;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;

public enum SkillExecutorNoEffect implements ISkillExecutor {
	INSTANCE;

	@Override
	public void execute(final Skill skill, final IBattleContext context, final BattleCommand battleCommand,
			final int player, final int character) {
		final CharacterState user = context.getCharacterState(player, character);
		final BattleAction action = new BattleAction(user.getId(), ArrayUtils.EMPTY_STRING_ARRAY,
				String.format("%s used %s.", user.getNickname(), skill.getName()), "But nothing happended.");
		context.getBattleActions(player).add(action);
		context.getBattleActionsOpponent(player).add(action);
	}
}