package com.github.blutorange.translune.serial;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.common.StringUtil;
import com.github.blutorange.translune.db.Character;
import com.github.blutorange.translune.db.CharacterBuilder;
import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.logic.EExperienceGroup;

public class CharacterCsvModel {

	private final CharacterBuilder builder;

	public CharacterCsvModel() {
		this.builder = new CharacterBuilder();
	}

	public void addSkill(final int level, final Skill skill) {
		builder.addSkill(level, skill);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(@Nullable final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CharacterCsvModel))
			return false;
		final CharacterCsvModel other = (CharacterCsvModel) obj;
		if (builder.getId() == null || other.builder.getId() == null)
			throw new IllegalStateException("name must be set");
		if (!builder.getId().equals(other.builder.getId()))
			return false;
		return true;
	}

	public String getKey() {
		final String id = builder.getId();
		if (id == null)
			throw new IllegalStateException("name must be set");
		return id;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (builder.getId() == null)
			throw new IllegalStateException("name must be set");
		final int prime = 31;
		int result = 1;
		result = prime * result + builder.getId().hashCode();
		return result;
	}

	public void setAccuracy(final int accuracy) {
		builder.setAccuracy(accuracy);
	}

	/**
	 * @param cry
	 *            the cry to set
	 */
	public void setCry(final String cry) {
		builder.setCry(cry);
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		builder.setDescription(description);
	}

	/**
	 * @param elements
	 *            the elements to set
	 */
	public void setElements(final String elements) {
		builder.addElements(Arrays.stream(elements.split(",")).map(String::trim).map(StringUtil::toRootUpperCase)
				.map(EElement::valueOf).collect(Collectors.toSet()));
	}

	public void setEvasion(final int evasion) {
		builder.setEvasion(evasion);
	}

	/**
	 * @param experienceGroup
	 *            the experienceGroup to set
	 */
	public void setExperienceGroup(final String experienceGroup) {
		builder.setExperienceGroup(EExperienceGroup.valueOf(experienceGroup));
	}

	/**
	 * @param imgBack
	 *            the imgBack to set
	 */
	public void setImgBack(final String imgBack) {
		builder.setImgBack(imgBack);
	}

	/**
	 * @param imgFront
	 *            the imgFront to set
	 */
	public void setImgFront(final String imgFront) {
		builder.setImgFront(imgFront);
	}

	public void setImgIcon(final String imgIcon) {
		builder.setImgIcon(imgIcon);
	}

	/**
	 * @param magicalAttack
	 *            the magicalAttack to set
	 */
	public void setMagicalAttack(final int magicalAttack) {
		builder.setMagicalAttack(magicalAttack);
	}

	/**
	 * @param magicalDefense
	 *            the magicalDefense to set
	 */
	public void setMagicalDefense(final int magicalDefense) {
		builder.setMagicalDefense(magicalDefense);
	}

	/**
	 * @param maxHp
	 *            the maxHp to set
	 */
	public void setMaxHp(final int maxHp) {
		builder.setMaxHp(maxHp);
	}

	/**
	 * @param maxMp
	 *            the maxMp to set
	 */
	public void setMaxMp(final int maxMp) {
		builder.setMaxMp(maxMp);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		builder.setName(name);
	}

	/**
	 * @param attack
	 *            the attack to set
	 */
	public void setPhysicalAttack(final int physicalAttack) {
		builder.setPhysicalAttack(physicalAttack);
	}

	/**
	 * @param defense
	 *            the defense to set
	 */
	public void setPhysicalDefense(final int physicalDefense) {
		builder.setPhysicalDefense(physicalDefense);
	}

	/**
	 * @param speed
	 *            the speed to set
	 */
	public void setSpeed(final int speed) {
		builder.setSpeed(speed);
	}

	public Character toEntity() {
		return builder.build();
	}
}