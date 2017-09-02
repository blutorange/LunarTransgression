package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.db.CharacterState;
import com.jsoniter.annotation.JsonIgnore;

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
	@JsonIgnore
	CharacterState getCharacterState();
	@JsonIgnore
	IComputedStatus getSnapshot();

	static IComputedStatus get(final CharacterState characterState) {
		return new ComputedStatus(characterState);
	}
}
