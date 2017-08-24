package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.socket.BattleCommand;

public interface ISkillExecutor {
	void execute(final Skill skill, final IBattleContext context, final BattleCommand battleCommand, final int player,
			final int character);
}
