package com.github.blutorange.translune.db;

import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.logic.EExperienceGroup;

public class ModifiableCharacter extends ModifiableEntity<Character> {
	public Set<EElement> getElements() {
		return entity.getElements();
	}

	public Map<Skill, Integer> getSkills() {
		return entity.getSkills();
	}

	public void setAccuracy(final int accuracy) {
		entity.setAccuracy(accuracy);
	}

	public void setCry(final String cry) {
		entity.setCry(cry);
	}

	public void setDescription(final String description) {
		entity.setDescription(description);
	}

	public void setElements(final Set<@NonNull EElement> elements) {
		entity.setElements(elements);
	}

	public void setEvasion(final int evasion) {
		entity.setEvasion(evasion);
	}

	public void setExperienceGroup(final EExperienceGroup experienceGroup) {
		entity.setExperienceGroup(experienceGroup);
	}

	public void setImgIcon(final String imgIcon) {
		entity.setImgIcon(imgIcon);
	}

	public void setImgFront(final String gifFront) {
		entity.setImgFront(gifFront);
	}

	public void setImgBack(final String gifBack) {
		entity.setImgBack(gifBack);
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

	public void setName(@NonNull final String name) {
		entity.setName(name);
	}

	public void setPhysicalAttack(final int physicalAttack) {
		entity.setPhysicalAttack(physicalAttack);
	}

	public void setPhysicalDefense(final int physicalDefense) {
		entity.setPhysicalDefense(physicalDefense);
	}

	public void setSkills(final Map<Skill, Integer> skills) {
		entity.setSkills(skills);
	}

	public void setSpeed(final int speed) {
		entity.setSpeed(speed);
	}
}