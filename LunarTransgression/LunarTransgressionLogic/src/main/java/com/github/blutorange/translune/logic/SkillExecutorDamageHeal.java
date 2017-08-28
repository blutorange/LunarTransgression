package com.github.blutorange.translune.logic;

import java.util.List;

import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.socket.BattleCommand;

public class SkillExecutorDamageHeal extends ASkillExecutor<EDamageHealFlavor> {
	@Override
	public void execute(final Skill skill, final IBattleContext context, final BattleCommand battleCommand,
			final int player, final int character) {
		final String useMessage = skill.getIsPhysical() ? "%s unleashes %s!" : "%s casts %s!";
		final String flavor = skill.getFlavor();
		EDamageHealFlavor damageHealFlavor = null;
		if (flavor != null) {
			try {
				damageHealFlavor = EDamageHealFlavor.valueOf(flavor);
			}
			catch (final IllegalArgumentException e) {
				damageHealFlavor = EDamageHealFlavor.NONE;
			}
		}
		else {
			damageHealFlavor = EDamageHealFlavor.NONE;
		}
		basic(useMessage, skill, context, battleCommand, player, character, damageHealFlavor);
	}

	@Override
	protected void afterDamage(final IComputedBattleStatus user, final IComputedBattleStatus target, final int damage,
			final List<String> messages, final IBattleContext context, final EDamageHealFlavor damageHealFlavor) {
		if (!damageHealFlavor.preconditionFulfilled(target))
			return;
		final int healPower = damageHealFlavor.getRelativeAmount() * damage / target.getComputedBattleHpAbsolute();
		battleProcessing.performHeal(() -> healPower, user, messages, context);
	}
}