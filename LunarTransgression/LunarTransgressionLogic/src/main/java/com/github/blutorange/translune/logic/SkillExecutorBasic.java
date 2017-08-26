package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.socket.BattleCommand;

public class SkillExecutorBasic extends ASkillExecutor {
	@Override
	public void execute(final Skill skill, final IBattleContext context, final BattleCommand battleCommand,
			final int player, final int character) {
		final String useMessage = skill.getIsPhysical() ? "%s unleashes %s!" : "%s casts %s!";
		basic(useMessage, skill, context, battleCommand, player, character);
	}
}