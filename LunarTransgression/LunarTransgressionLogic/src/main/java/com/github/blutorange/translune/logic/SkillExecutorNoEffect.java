package com.github.blutorange.translune.logic;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;

public enum SkillExecutorNoEffect implements ISkillExecutor {
	INSTANCE;

	@Override
	public void execute(final Skill skill, final IBattleContext context, final BattleCommand battleCommand,
			final List<BattleAction> battleActionsMe, final List<BattleAction> battleActionsHim, final int player,
			final int character) {
		final CharacterState cs = context.getCharacterState(player, character);
		final BattleAction action = new BattleAction(cs.getId(), ArrayUtils.EMPTY_STRING_ARRAY,
				String.format("%s used %s.", cs.getNickname(), skill.getName()), "But nothing happended.");
		battleActionsMe.add(action);
		battleActionsHim.add(action);
	}
}