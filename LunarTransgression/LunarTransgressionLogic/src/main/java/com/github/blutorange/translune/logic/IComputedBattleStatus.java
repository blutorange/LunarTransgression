package com.github.blutorange.translune.logic;

public interface IComputedBattleStatus extends IComputedStatus {
	int getComputedBattleAccuracy();
	int getComputedBattleEvasion();
	int getComputedBattleHp();
	int getComputedBattleMagicalAttack();
	int getComputedBattleMagicalDefense();
	int getComputedBattleMp();
	int getComputedBattlePhysicalAttack();
	int getComputedBattlePhysicalDefense();
	int getComputedBattleSpeed();
}
