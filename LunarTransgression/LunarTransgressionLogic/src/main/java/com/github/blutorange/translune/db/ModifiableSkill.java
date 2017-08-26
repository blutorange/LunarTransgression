package com.github.blutorange.translune.db;

import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.translune.logic.EActionTarget;
import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.logic.ESkillEffect;
import com.github.blutorange.translune.logic.EStatusCondition;

public class ModifiableSkill extends ModifiableEntity<Skill> {
	public void addStageChange(final EStatusValue statusValue, final int stagePower) {
		entity.getStageChanges().put(statusValue, Integer.valueOf(stagePower));
	}

	public void clearStageChanges() {
		entity.getStageChanges().clear();
	}

	public void removeStageChange(final EStatusValue statusValue) {
		entity.getStageChanges().remove(statusValue);
	}

	public void setAccuracy(final int accuracy) {
		entity.setAccuracy(accuracy);
	}

	public void setAlwaysHits(final boolean alwaysHits) {
		entity.setAlwaysHits(alwaysHits);
	}

	public void setAttackPower(final int attackPower) {
		entity.setAttackPower(attackPower);
	}

	public void setCondition(final EStatusCondition condition) {
		entity.setCondition(condition);
	}

	public void setConditionChance(final int conditionChance) {
		entity.setConditionChance(conditionChance);
	}

	public void setDescription(final String description) {
		entity.setDescription(description);
	}

	public void setEffect(final ESkillEffect effect) {
		entity.setEffect(effect);
	}

	public void setElement(final EElement element) {
		entity.setElement(element);
	}

	public void setFlavor(final String flavor) {
		entity.setFlavor(flavor);
	}

	public void setHealPower(final int healPower) {
		entity.setHealPower(healPower);
	}

	public void setHighCritical(final boolean highCritical) {
		entity.setHighCritical(highCritical);
	}

	public void setIsPhysical(final boolean isPhysical) {
		entity.setIsPhysical(isPhysical);
	}

	public void setMp(final int mp) {
		entity.setMp(mp);
	}

	public void setName(@NonNull final String name) {
		entity.setName(name);
	}

	public void setPriority(final int priority) {
		entity.setPriority(priority);
	}

	public void setTarget(final EActionTarget target) {
		entity.setTarget(target);
	}
}