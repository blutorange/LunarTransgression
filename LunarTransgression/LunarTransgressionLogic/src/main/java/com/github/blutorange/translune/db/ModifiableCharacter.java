package com.github.blutorange.translune.db;

import java.util.Map;
import java.util.Set;

import com.github.blutorange.translune.logic.EElement;

public class ModifiableCharacter extends ModifiableEntity<Character> {
	public Set<EElement> getElements() {
		return entity.getElements();
	}

	public Map<Integer, Skill> getSkills() {
		return entity.getSkills();
	}

	public void setAccuracy(final int accuracy) {
		entity.setAccuracy(accuracy);
	}

	public void setElements(final Set<EElement> elements) {
		entity.setElements(elements);
	}

	public void setEvasion(final int evasion) {
		entity.setEvasion(evasion);
	}

	public void setMagicalAttack(final int magicalAttack) {
		entity.setMagicalAttack(magicalAttack);
	}

	public void setMagicalDefense(final int magicalDefense) {
		entity.setMagicalDefense(magicalDefense);
	}

	public void setMaxHp(final int maxHp) {
		entity.setMaxHp(maxHp);
	}

	public void setMaxMp(final int maxMp) {
		entity.setMaxMp(maxMp);
	}

	public void setName(final String name) {
		entity.setName(name);
	}

	public void setPhysicalAttack(final int physicalAttack) {
		entity.setPhysicalAttack(physicalAttack);
	}

	public void setPhysicalDefense(final int physicalDefense) {
		entity.setPhysicalDefense(physicalDefense);
	}

	public void setSkills(final Map<Integer, Skill> skills) {
		entity.setSkills(skills);
	}

	public void setSpeed(final int speed) {
		entity.setSpeed(speed);
	}
}