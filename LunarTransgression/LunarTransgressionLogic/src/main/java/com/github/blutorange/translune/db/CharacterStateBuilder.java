package com.github.blutorange.translune.db;

import java.util.Random;

import org.apache.commons.lang3.builder.Builder;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.common.MathUtil;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.logic.ENature;
import com.github.blutorange.translune.util.Constants;

public class CharacterStateBuilder implements Builder<CharacterState> {
	private Character character;
	private int exp = 0;
	private int ivHp;
	private int ivMagicalAttack;
	private int ivMagicalDefense;
	private int ivMp;
	private int ivPhysicalAttack;
	private int ivPhysicalDefense;
	private int ivSpeed;
	private int level = Constants.MIN_LEVEL;
	private ENature nature;
	@Nullable
	private String nickname;

	public CharacterStateBuilder() {
	}

	@Override
	public CharacterState build() {
		String nickname = this.nickname;
		if (character == null)
			throw new IllegalStateException("no character set");
		if (nature == null)
			throw new IllegalStateException("no nature set");
		if (nickname == null || nickname.isEmpty())
			nickname = character.getName();
		final CharacterState characterState = new CharacterState();
		characterState.setCharacter(character);
		characterState.setExp(exp);
		characterState.setIvHp(ivHp);
		characterState.setIvMagicalAttack(ivMagicalAttack);
		characterState.setIvMagicalDefense(ivMagicalDefense);
		characterState.setIvMp(ivMp);
		characterState.setIvPhysicalAttack(ivPhysicalAttack);
		characterState.setIvPhysicalDefense(ivPhysicalDefense);
		characterState.setIvSpeed(ivSpeed);
		characterState.setLevel(level);
		characterState.setNature(nature);
		characterState.setNickname(nickname);
		return characterState;
	}

	public CharacterStateBuilder randomIvs() {
		final Random r = ComponentFactory.getLunarComponent().randomBasic().get();
		ivHp = r.nextInt(32);
		ivMp = r.nextInt(32);
		ivPhysicalAttack = r.nextInt(32);
		ivPhysicalDefense = r.nextInt(32);
		ivMagicalAttack = r.nextInt(32);
		ivMagicalDefense = r.nextInt(32);
		ivSpeed = r.nextInt(32);
		return this;
	}

	public CharacterStateBuilder randomNature() {
		final ENature[] natures = ENature.values();
		final Random r = ComponentFactory.getLunarComponent().randomBasic().get();
		this.nature = natures[r.nextInt(natures.length)];
		return this;
	}

	public CharacterStateBuilder setCharacter(final Character character) {
		this.character = character;
		return this;
	}

	public CharacterStateBuilder setExp(final int exp) {
		this.exp = MathUtil.clamp(exp, 0, Constants.MAX_EXP);
		return this;
	}

	public CharacterStateBuilder setLevel(final int level) {
		this.level = MathUtil.clamp(level, Constants.MIN_LEVEL, Constants.MAX_LEVEL);
		return this;
	}

	public CharacterStateBuilder setNickname(final String nickname) {
		this.nickname = nickname;
		return this;
	}

	public CharacterStateBuilder setNature(final ENature nature) {
		this.nature = nature;
		return this;
	}

	public CharacterStateBuilder setIvHp(final int ivHp) {
		this.ivHp = ivHp;
		return this;
	}

	public CharacterStateBuilder setIvMp(final int ivMp) {
		this.ivMp = ivMp;
		return this;
	}

	public CharacterStateBuilder setIvPhysicalAttack(final int ivPhysicalAttack) {
		this.ivPhysicalAttack = ivPhysicalAttack;
		return this;
	}

	public CharacterStateBuilder setIvPhysicalDefense(final int ivPhysicalDefense) {
		this.ivPhysicalDefense = ivPhysicalDefense;
		return this;
	}

	public CharacterStateBuilder setIvMagicalAttack(final int ivMagicalAttack) {
		this.ivMagicalAttack = ivMagicalAttack;
		return this;
	}

	public CharacterStateBuilder setIvMagicalDefense(final int ivMagicalDefense) {
		this.ivMagicalDefense = ivMagicalDefense;
		return this;
	}

	public CharacterStateBuilder setIvSpeed(final int ivSpeed) {
		this.ivSpeed = ivSpeed;
		return this;
	}
}