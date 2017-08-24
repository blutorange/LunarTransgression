package com.github.blutorange.translune.logic;

public interface IDamageResult {
	int getDamage();
	boolean isCritial();
	boolean isStab();
	ETypeEffectiveness getTypeEffectiveness();
}
