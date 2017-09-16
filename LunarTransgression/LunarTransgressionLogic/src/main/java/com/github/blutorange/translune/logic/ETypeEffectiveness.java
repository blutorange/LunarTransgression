package com.github.blutorange.translune.logic;

import org.apache.commons.math3.fraction.BigFraction;
import org.eclipse.jdt.annotation.Nullable;

public enum ETypeEffectiveness {
	INEFFECTIVE(BigFraction.ZERO, "It had no effect."),
	NOT_VERY_EFFECTIVE(BigFraction.ONE_HALF, "It was not very effective."),
	NORMALLY_EFFECTIVE(BigFraction.ONE, null),
	SUPER_EFFECTIVE(BigFraction.getReducedFraction(2,1), "It was super effective!"),
	HYPER_EFFECTIVE(BigFraction.getReducedFraction(4,1), "It was super effective!")
	;

	private BigFraction multiplier;
	@Nullable
	private String battleMessage;

	private ETypeEffectiveness(final BigFraction multiplier, @Nullable final String battleMessage) {
		this.multiplier = multiplier;
		this.battleMessage = battleMessage;
	}

	public BigFraction getMultiplier() {
		return multiplier;
	}

	@Nullable
	public String getBattleMessage() {
		return battleMessage;
	}
}
