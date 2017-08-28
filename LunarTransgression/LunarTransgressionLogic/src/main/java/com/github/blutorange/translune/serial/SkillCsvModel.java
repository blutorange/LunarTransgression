package com.github.blutorange.translune.serial;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.EStatusValue;
import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.db.SkillBuilder;
import com.github.blutorange.translune.logic.EActionTarget;
import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.logic.ESkillEffect;
import com.github.blutorange.translune.logic.EStatusCondition;
import com.jsoniter.JsonIterator;

public class SkillCsvModel {
	private final SkillBuilder builder;

	public SkillCsvModel() {
		this.builder = new SkillBuilder();
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
		final SkillCsvModel other = (SkillCsvModel) obj;
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

	/**
	 * @param accuracy
	 *            the accuracy to set
	 */
	public void setAccuracy(final int accuracy) {
		builder.setAccuracy(accuracy);
	}

	public void setAlwaysHits(final boolean alwaysHits) {
		builder.setAlwaysHits(alwaysHits);
	}

	public void setAttackPower(final int power) {
		builder.setAttackPower(power);
	}

	public void setCondition(final EStatusCondition condition) {
		builder.setCondition(condition);
	}

	public void setConditionChance(final int conditionChance) {
		builder.setConditionChance(conditionChance);
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

	public void setFlavor(final String flavor) {
		builder.setFlavor(flavor);
	}

	public void setFlinchChance(final int flinchChance) {
		builder.setFlinchChance(flinchChance);
	}

	public void setHealPower(final int healPower) {
		builder.setHealPower(healPower);
	}

	/**
	 * @param highCritical
	 *            the highCritical to set
	 */
	public void setHighCritical(final boolean highCritical) {
		builder.setHighCritical(highCritical);
	}

	/**
	 * @param isPhysical
	 *            the isPhysical to set
	 */
	public void setIsPhysical(final boolean isPhysical) {
		builder.setIsPhysical(isPhysical);
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

	public void setPriority(final int priority) {
		builder.setPriority(priority);
	}

	public void setStageChance(final int stageChance) {
		builder.setStageChance(stageChance);
	}

	public void setStageChanges(@Nullable final String stageChanges) {
		if (stageChanges == null || stageChanges.isEmpty())
			return;
		final Map<EStatusValue, Integer> map = JsonIterator.deserialize(stageChanges).asMap().entrySet().stream()
				.collect(Collectors.toMap(e -> EStatusValue.valueOf(e.getKey().toUpperCase(Locale.ROOT)),
						e -> Integer.valueOf(e.getValue().toString())));
		builder.addStageChanges(map);
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