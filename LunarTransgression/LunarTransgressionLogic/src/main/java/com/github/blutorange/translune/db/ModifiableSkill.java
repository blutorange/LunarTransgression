package com.github.blutorange.translune.db;

import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.logic.ESkillEffect;

public class ModifiableSkill extends ModifiableEntity<Skill> {
	public void setLevel(final int level) {
		entity.setLevel(level);
	}

	public void setEffect(final ESkillEffect effect) {
		entity.setEffect(effect);
	}

	public void setElement(final EElement element) {
		entity.setElement(element);
	}

	public void setName(final String name) {
		entity.setName(name);
	}

	public void setPower(final int power) {
		entity.setPower(power);
	}
}