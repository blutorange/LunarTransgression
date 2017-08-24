package com.github.blutorange.translune.logic;

import org.apache.commons.lang3.math.Fraction;
import org.eclipse.jdt.annotation.Nullable;

public enum ETypeEffectiveness {
	INEFFECTIVE(Fraction.ZERO, "It had no effect."),
	NOT_VERY_EFFECTIVE(Fraction.ONE_HALF, "It was not very effective."),
	NORMALLY_EFFECTIVE(Fraction.ONE, null),
	SUPER_EFFECTIVE(Fraction.getFraction(2,1), "It was super effective!"),
	HYPER_EFFECTIVE(Fraction.getFraction(4,1), "It was super effective!")
	;

	private Fraction multiplier;
	@Nullable
	private String battleMessage;

	private ETypeEffectiveness(final Fraction multiplier, @Nullable final String battleMessage) {
		this.multiplier = multiplier;
		this.battleMessage = battleMessage;
	}

	public Fraction getMultiplier() {
		return multiplier;
	}

	@Nullable
	public String getBattleMessage() {
		return battleMessage;
	}
}
