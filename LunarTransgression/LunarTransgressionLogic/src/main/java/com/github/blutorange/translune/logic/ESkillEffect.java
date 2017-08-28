package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.socket.BattleCommand;

// TODO implement FIELD_EFFECT(14), FORCE_SWITCH(2), WHOLE_FIELD_EFFECT(16)
public enum ESkillEffect implements ISkillExecutor {
	AILMENT(new SkillExecutorBasic()),
	DAMAGE(new SkillExecutorBasic()),
	DAMAGE_AILMENT(new SkillExecutorBasic()),
	DAMAGE_HEAL(new SkillExecutorDamageHeal()),
	DAMAGE_LOWER(new SkillExecutorBasic()),
	DAMAGE_RAISE(new SkillExecutorBasic()),
	FIELD_EFFECT(new SkillExecutorNoEffect()),
	FORCE_SWITCH(new SkillExecutorNoEffect()),
	HEAL(new SkillExecutorHeal()),
	NET_GOOD_STATS(new SkillExecutorBasic()),
	NONE(new SkillExecutorNoEffect()),
	OHKO(new SkillExecutorOhko()),
	SWAGGER(new SkillExecutorBasic()),
	UNIQUE(new SkillExecutorUnique()),
	WHOLE_FIELD_EFFECT(new SkillExecutorNoEffect()),
	;

	private ISkillExecutor executor;

	private ESkillEffect(final ISkillExecutor executor) {
		this.executor = executor;
	}

	@Override
	public void execute(final Skill skill, final IBattleContext context, final BattleCommand battleCommand,
			final int player, final int character) {
		executor.execute(skill, context, battleCommand, player, character);
	}
}