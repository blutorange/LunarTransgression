package com.github.blutorange.translune.logic;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.common.EVoid;
import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.socket.BattleCommand;

public class SkillExecutorOhko extends ASkillExecutor<EVoid> {
	@Override
	public void execute(final Skill skill, final IBattleContext context, final BattleCommand battleCommand,
			final int player, final int character) {
		final String useMessage = skill.getIsPhysical() ? "%s unleashes %s!" : "%s casts %s!";
		basic(useMessage, skill, context, battleCommand, player, character, EVoid.INSTANCE);
	}

	@Override
	protected IDamageResult @Nullable [] damageResultOnNoAttackPower(final Skill skill, final IComputedBattleStatus user,
			final IComputedBattleStatus[] targets) {
		final IDamageResult[] result = new IDamageResult[targets.length];
		for (int i = result.length; i --> 0;)
			result[i] = new OhkoDamageResult(targets[i]);
		return result;
	}

	private static class OhkoDamageResult implements IDamageResult {
		private final int damage;

		public OhkoDamageResult(final IComputedBattleStatus target) {
			damage = target.getComputedBattleMaxHp();
		}

		@Override
		public int getDamage() {
			return damage;
		}

		@Override
		public boolean isCritial() {
			return false;
		}

		@Override
		public boolean isStab() {
			return false;
		}

		@Override
		public ETypeEffectiveness getTypeEffectiveness() {
			return ETypeEffectiveness.NORMALLY_EFFECTIVE;
		}
	}
}
