package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.db.CharacterState;

public interface IComputedStatus {
	int getComputedAccuracy();
	int getComputedEvasion();
	int getComputedMaxHp();
	int getComputedMagicalAttack();
	int getComputedMagicalDefense();
	int getComputedMaxMp();
	int getComputedPhysicalAttack();
	int getComputedPhysicalDefense();
	int getComputedSpeed();
	CharacterState getCharacterState();
}
