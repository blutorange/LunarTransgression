package com.github.blutorange.translune.serial;

import java.util.Locale;

import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.db.SkillBuilder;
import com.github.blutorange.translune.logic.EActionTarget;
import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.logic.ESkillEffect;

public class SkillCsvModel {
	private SkillBuilder builder;

	public SkillCsvModel() {
		this.builder = new SkillBuilder();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CharacterCsvModel))
			return false;
		final SkillCsvModel other = (SkillCsvModel) obj;
		if (builder.getId() == null || other.builder.getId() == null)
			throw new IllegalStateException("name must be set");
		if (!builder.getId().equals(other.builder.getId()))
			return false;
		return true;
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

	public String getKey() {
		String id = builder.getId();
		if (id == null)
			throw new IllegalStateException("name must be set");
		return id;
	}
	
	
	/**
	 * @param accuracy
	 *            the accuracy to set
	 */
	public void setAccuracy(final int accuracy) {
		builder.setAccuracy(accuracy);
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		builder.setDescription(description);
	}

	/**
	 * @param effect
	 *            the effect to set
	 */
	public void setEffect(final String effect) {
		builder.setEffect(ESkillEffect.valueOf(effect.toUpperCase(Locale.ROOT)));
	}

	/**
	 * @param element
	 *            the element to set
	 */
	public void setElement(final String element) {
		builder.setElement(EElement.valueOf(element.toUpperCase(Locale.ROOT)));
	}

	/**
	 * @param highCritical
	 *            the highCritical to set
	 */
	public void setHighCritical(final boolean highCritical) {
		builder.setHighCritical(highCritical);
	}

	/**
	 * @param mp
	 *            the mp to set
	 */
	public void setMp(final int mp) {
		builder.setMp(mp);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		builder.setName(name);
	}

	/**
	 * @param isPhysical
	 *            the isPhysical to set
	 */
	public void setIsPhysical(final boolean isPhysical) {
		builder.setIsPhysical(isPhysical);
	}

	/**
	 * @param power
	 *            the power to set
	 */
	public void setPower(final int power) {
		builder.setPower(power);
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(final int priority) {
		builder.setPriority(priority);
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(final String target) {
		builder.setTarget(EActionTarget.valueOf(target.toUpperCase(Locale.ROOT)));
	}

	public Skill toEntity() {
		return builder.build();
	}
}