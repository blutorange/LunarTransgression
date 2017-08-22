package com.github.blutorange.translune.db;

import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.logic.ESkillEffect;
import com.github.blutorange.translune.logic.ESkillTarget;

public class ModifiableSkill extends ModifiableEntity<Skill> {
	public void setAccuracy(final int accuracy) {
		entity.setAccuracy(accuracy);
	}

	public void setEffect(final ESkillEffect effect) {
		entity.setEffect(effect);
	}

	public void setElement(final EElement element) {
		entity.setElement(element);
	}

	public void setIsPhysical(final boolean isPhysical) {
		entity.setIsPhysical(isPhysical);
	}

	public void setName(final String name) {
		entity.setName(name);
	}

	public void setPower(final int power) {
		entity.setPower(power);
	}

	public void setTtarget(final ESkillTarget target) {
		entity.setTarget(target);
	}

	void setMp(final int mp) {
		entity.setMp(mp);
	}

	void setPriority(final int priority) {
		entity.setPriority(priority);
	}
}