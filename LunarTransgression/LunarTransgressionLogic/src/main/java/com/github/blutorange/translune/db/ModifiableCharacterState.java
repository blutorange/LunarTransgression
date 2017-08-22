package com.github.blutorange.translune.db;

import com.github.blutorange.translune.logic.ENature;

public class ModifiableCharacterState extends ModifiableEntity<CharacterState> {
	public void setCharacter(final Character character) {
		entity.setCharacter(character);
	}

	public void setExp(final int exp) {
		entity.setExp(exp);
	}

	public void setId(final String id) {
		entity.setId(id);
	}

	/**
	 * @param ivHp
	 *            the ivHp to set
	 */
	public void setIvHp(final int ivHp) {
		entity.setIvHp(ivHp);
	}

	/**
	 * @param ivMagicalAttack
	 *            the ivMagicalAttack to set
	 */
	public void setIvMagicalAttack(final int ivMagicalAttack) {
		entity.setIvMagicalAttack(ivMagicalAttack);
	}

	/**
	 * @param ivMagicalDefense
	 *            the ivMagicalDefense to set
	 */
	public void setIvMagicalDefense(final int ivMagicalDefense) {
		entity.setIvMagicalDefense(ivMagicalDefense);
	}

	/**
	 * @param ivMp
	 *            the ivMp to set
	 */
	public void setIvMp(final int ivMp) {
		entity.setIvMp(ivMp);
	}

	/**
	 * @param ivPhysicalAttack
	 *            the ivPhysicalAttack to set
	 */
	public void setIvPhysicalAttack(final int ivPhysicalAttack) {
		entity.setIvPhysicalAttack(ivPhysicalAttack);
	}

	/**
	 * @param ivPhysicalDefense
	 *            the ivPhysicalDefense to set
	 */
	public void setIvPhysicalDefense(final int ivPhysicalDefense) {
		entity.setIvPhysicalDefense(ivPhysicalDefense);
	}

	/**
	 * @param ivSpeed
	 *            the ivSpeed to set
	 */
	public void setIvSpeed(final int ivSpeed) {
		entity.setIvSpeed(ivSpeed);
	}

	public void setLevel(final int level) {
		entity.setLevel(level);
	}

	public void setNature(final ENature nature) {
		entity.setNature(nature);
	}

	public void setNickname(final String nickname) {
		entity.setNickname(nickname);
	}

	public void setPlayer(final Player player) {
		entity.setPlayer(player);
	}
}