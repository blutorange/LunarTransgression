package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.socket.BattleCommand;

public class SkillExecutorUnique extends ASkillExecutor {

	@Override
	public void execute(final Skill skill, final IBattleContext context, final BattleCommand battleCommand,
			final int player, final int character) {
		ISkillExecutor executor = null;
		final String flavor = skill.getFlavor();
		if (flavor != null) {
			try {
				executor = EUniqueSkill.valueOf(flavor);
			}
			catch (final IllegalArgumentException e) {
				executor = new SkillExecutorNoEffect();
			}
		}
		if (executor == null)
			executor = new SkillExecutorNoEffect();
		executor.execute(skill, context, battleCommand, player, character);
	}

	//TODO implement these UNIQUE skills
	private static enum EUniqueSkill implements ISkillExecutor {
		ACUPRESSURE(new SkillExecutorNoEffect()),
		AFTER_YOU(new SkillExecutorNoEffect()),
		ALLY_SWITCH(new SkillExecutorNoEffect()),
		AQUA_RING(new SkillExecutorNoEffect()),
		AROMATHERAPY(new SkillExecutorNoEffect()),
		ASSIST(new SkillExecutorNoEffect()),
		BATON_PASS(new SkillExecutorNoEffect()),
		BELLY_DRUM(new SkillExecutorNoEffect()),
		BESTOW(new SkillExecutorNoEffect()),
		BLOCK(new SkillExecutorNoEffect()),
		CAMOUFLAGE(new SkillExecutorNoEffect()),
		CELEBRATE(new SkillExecutorNoEffect()),
		CONVERSION(new SkillExecutorNoEffect()),
		CONVERSION_2(new SkillExecutorNoEffect()),
		COPYCAT(new SkillExecutorNoEffect()),
		CURSE(new SkillExecutorNoEffect()),
		DEFOG(new SkillExecutorNoEffect()),
		DESTINY_BOND(new SkillExecutorNoEffect()),
		DETECT(new SkillExecutorNoEffect()),
		DISABLE(new SkillExecutorNoEffect()),
		DOOM_DESIRE(new SkillExecutorNoEffect()),
		ELECTRIFY(new SkillExecutorNoEffect()),
		ENCORE(new SkillExecutorNoEffect()),
		ENDURE(new SkillExecutorNoEffect()),
		ENTRAINMENT(new SkillExecutorNoEffect()),
		FLOWER_SHIELD(new SkillExecutorNoEffect()),
		FOCUS_ENERGY(new SkillExecutorNoEffect()),
		FOLLOW_ME(new SkillExecutorNoEffect()),
		FORESTS_CURSE(new SkillExecutorNoEffect()),
		FUTURE_SIGHT(new SkillExecutorNoEffect()),
		GASTRO_ACID(new SkillExecutorNoEffect()),
		GRUDGE(new SkillExecutorNoEffect()),
		GUARD_SPLIT(new SkillExecutorNoEffect()),
		GUARD_SWAP(new SkillExecutorNoEffect()),
		HAPPY_HOUR(new SkillExecutorNoEffect()),
		HEAL_BELL(new SkillExecutorNoEffect()),
		HEALING_WISH(new SkillExecutorNoEffect()),
		HEART_SWAP(new SkillExecutorNoEffect()),
		HELPING_HAND(new SkillExecutorNoEffect()),
		HOLD_HANDS(new SkillExecutorNoEffect()),
		IMPRISON(new SkillExecutorNoEffect()),
		KINGS_SHIELD(new SkillExecutorNoEffect()),
		LOCK_ON(new SkillExecutorNoEffect()),
		LUNAR_DANCE(new SkillExecutorNoEffect()),
		MAGIC_COAT(new SkillExecutorNoEffect()),
		MAGNET_RISE(new SkillExecutorNoEffect()),
		MEAN_LOOK(new SkillExecutorNoEffect()),
		MEMENTO(new SkillExecutorNoEffect()),
		METRONOME(new SkillExecutorNoEffect()),
		MIMIC(new SkillExecutorNoEffect()),
		MIND_READER(new SkillExecutorNoEffect()),
		MIRROR_MOVE(new SkillExecutorNoEffect()),
		NATURE_POWER(new SkillExecutorNoEffect()),
		PAIN_SPLIT(new SkillExecutorNoEffect()),
		POWDER(new SkillExecutorNoEffect()),
		POWER_SPLIT(new SkillExecutorNoEffect()),
		POWER_SWAP(new SkillExecutorNoEffect()),
		POWER_TRICK(new SkillExecutorNoEffect()),
		PROTECT(new SkillExecutorNoEffect()),
		PSYCH_UP(new SkillExecutorNoEffect()),
		PSYCHO_SHIFT(new SkillExecutorNoEffect()),
		QUASH(new SkillExecutorNoEffect()),
		RAGE_POWDER(new SkillExecutorNoEffect()),
		RECYCLE(new SkillExecutorNoEffect()),
		REFLECT_TYPE(new SkillExecutorNoEffect()),
		REFRESH(new SkillExecutorNoEffect()),
		REST(new SkillExecutorNoEffect()),
		ROLE_PLAY(new SkillExecutorNoEffect()),
		SHELL_SMASH(new SkillExecutorNoEffect()),
		SIMPLE_BEAM(new SkillExecutorNoEffect()),
		SKETCH(new SkillExecutorNoEffect()),
		SKILL_SWAP(new SkillExecutorNoEffect()),
		SLEEP_TALK(new SkillExecutorNoEffect()),
		SNATCH(new SkillExecutorNoEffect()),
		SOAK(new SkillExecutorNoEffect()),
		SPIDER_WEB(new SkillExecutorNoEffect()),
		SPIKY_SHIELD(new SkillExecutorNoEffect()),
		SPITE(new SkillExecutorNoEffect()),
		SPLASH(new SkillExecutorNoEffect()),
		STOCKPILE(new SkillExecutorNoEffect()),
		SUBSTITUTE(new SkillExecutorNoEffect()),
		SWITCHEROO(new SkillExecutorNoEffect()),
		TAUNT(new SkillExecutorNoEffect()),
		TELEPORT(new SkillExecutorNoEffect()),
		TOPSY_TURVY(new SkillExecutorNoEffect()),
		TRANSFORM(new SkillExecutorNoEffect()),
		TRICK(new SkillExecutorNoEffect()),
		TRICK_OR_TREAT(new SkillExecutorNoEffect()),
		WISH(new SkillExecutorNoEffect()),
		WORRY_SEED(new SkillExecutorNoEffect()),
		;
		private final ISkillExecutor executor;

		private EUniqueSkill(final ISkillExecutor executor) {
			this.executor = executor;
		}

		@Override
		public void execute(final Skill skill, final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			executor.execute(skill, context, battleCommand, player, character);
		}
	}
}
