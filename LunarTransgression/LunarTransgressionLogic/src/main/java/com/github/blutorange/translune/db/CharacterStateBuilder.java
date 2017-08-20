package com.github.blutorange.translune.db;

import java.util.Random;

import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.logic.ENature;
import com.github.blutorange.translune.util.Constants;

public class CharacterStateBuilder {
	private String nickname;
	private Character character;
	private final int hp = Constants.MAX_RELATIVE_HP;
	private final int mp = Constants.MAX_RELATIVE_MP;
	private int ivHp;
	private int ivMp;
	private int ivPhysicalAttack;
	private int ivPhysicalDefense;
	private int ivMagicalAttack;
	private int ivMagicalDefense;
	private int ivSpeed;
	private int exp = 0;
	private int level = Constants.MIN_LEVEL;
	private ENature nature;

	public CharacterStateBuilder() {
	}

	public CharacterStateBuilder setExp(final int exp) {
		this.exp = exp < 0 ? 0 : exp > Constants.MAX_EXP ? Constants.MAX_EXP : exp;
		return this;
	}

	public CharacterStateBuilder setCharacter(final Character character) {
		this.character = character;
		return this;
	}

	public CharacterStateBuilder setLevel(final int level) {
		this.level = level < Constants.MIN_LEVEL ? Constants.MIN_LEVEL
				: level > Constants.MAX_LEVEL ? Constants.MAX_LEVEL : level;
		return this;
	}

	public CharacterStateBuilder setNickname(final String nickname) {
		this.nickname = nickname;
		return this;
	}

	public CharacterStateBuilder randomNature() {
		final ENature[] natures = ENature.values();
		final Random r = ComponentFactory.getLogicComponent().randomBasic();
		this.nature = natures[r.nextInt(natures.length)];
		return this;
	}

	public CharacterStateBuilder randomIvs() {
		final Random r = ComponentFactory.getLogicComponent().randomBasic();
		ivHp = r.nextInt(32);
		ivMp = r.nextInt(32);
		ivPhysicalAttack = r.nextInt(32);
		ivPhysicalDefense = r.nextInt(32);
		ivMagicalAttack = r.nextInt(32);
		ivMagicalDefense = r.nextInt(32);
		ivSpeed = r.nextInt(32);
		return this;
	}

	public CharacterState build() {
		if (character == null)
			throw new IllegalStateException("no character set");
		if (nickname.isEmpty())
			nickname = character.getName();
		return new CharacterState(character, nickname, nature, exp, level, hp, mp, ivHp, ivMp, ivPhysicalAttack,
				ivPhysicalDefense, ivMagicalAttack, ivMagicalDefense, ivSpeed);
	}
}