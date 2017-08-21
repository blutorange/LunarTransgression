package com.github.blutorange.translune.logic;

public enum ENature {
	//      hp mp pa pd ma md sp
	HARDY  (10,10,10,10,10,10,10),
	LONELY (10,10,11, 9,10,10,10),
	BRAVE  (10,10,11,10,10,10, 9),
	ADAMANT(10,10,11,10, 9,10,10),
	NAUGHTY(10,10,11,10,10, 9,10),
	BOLD   (10,10, 9,11,10,10,10),
	DOCILE (10,10,10,10,10,10,10),
	RELAXED(10,10,10,11,10,10, 9),
	IMPISH (10,10,10,11, 9,10,10),
	LAX    (10,10,10,11,10, 9,10),
	TIMID  (10,10, 9,10,10,10,11),
	HASTY  (10,10,10, 9,10,10,11),
	SERIOUS(10,10,10,10,10,10,10),
	JOLLY  (10,10,10,10, 9,10,11),
	NAIVE  (10,10,10,10,10, 9,11),
	MODEST (10,10, 9,10,11,10,10),
	MILD   (10,10,10, 9,11,10,10),
	QUIET  (10,10,10,10,11,10, 9),
	BASHFUL(10,10,10,10,10,10,10),
	RASH   (10,10,10,10,11, 9,10),
	CALM   (10,10, 9,10,10,11,10),
	GENTLE (10,10,10, 9,10,11,10),
	SASSY  (10,10,10,10,10,11, 9),
	CAREFUL(10,10,10,10, 9,11,10),
	QUIRKY (10,10,10,10,10,10,10),
	;

	private int speed;
	private int magicalDefense;
	private int magicalAttack;
	private int physicalDefense;
	private int physicalAttack;
	private int mp;
	private int hp;

	private ENature(final int hp, final int mp, final int physicalAttack, final int physicalDefense, final int magicalAttack, final int magicalDefense, final int speed) {
		this.hp = hp;
		this.mp = mp;
		this.physicalAttack = physicalAttack;
		this.physicalDefense = physicalDefense;
		this.magicalAttack = magicalAttack;
		this.magicalDefense = magicalDefense;
		this.speed = speed;
	}

	int adjustMp(final int mp) {
		return mp * this.mp / 10;
	}

	int adjustHp(final int hp) {
		return hp * this.hp / 10;
	}

	int adjustPhysicalAttack(final int physicalAttack) {
		return physicalAttack * this.physicalAttack / 10;
	}

	int adjustPhysicalDefense(final int physicalDefense) {
		return physicalDefense * this.physicalDefense / 10;
	}

	int adjustMagicalAttack(final int magicalAttack) {
		return magicalAttack * this.magicalAttack / 10;
	}

	int adjustMagicalDefense(final int magicalDefense) {
		return magicalDefense * this.magicalDefense / 10;
	}

	int adjustSpeed(final int speed) {
		return speed * this.speed / 10;
	}
}
