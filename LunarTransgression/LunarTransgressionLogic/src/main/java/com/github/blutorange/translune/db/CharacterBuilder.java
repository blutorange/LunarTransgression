package com.github.blutorange.translune.db;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.Builder;

import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.logic.EExperienceGroup;

public class CharacterBuilder implements Builder<Character> {
	private int accuracy;
	private String cry;
	private String description = StringUtils.EMPTY;
	private Set<EElement> elements = new HashSet<>();
	private int evasion;
	private EExperienceGroup experienceGroup;
	private String imgBack;
	private String imgFront;
	private int magicalAttack = -1;
	private int magicalDefense = -1;
	private int maxHp = -1;
	private int maxMp = -1;
	private String name;
	private int physicalAttack = -1;
	private int physicalDefense =  -1;
	private Map<Skill, Integer> skills = new HashMap<>();
	private int speed;

	public CharacterBuilder addElement(EElement element) {
		this.elements.add(element);
		return this;
	}

	public CharacterBuilder addElements(Set<EElement> elements) {
		this.elements.addAll(elements);
		return this;
	}

	public CharacterBuilder addSkill(int level, Skill skill) {
		this.skills.put(skill, Integer.valueOf(level));
		return this;
	}

	@Override
	public Character build() {
		if (this.accuracy < 0)
			throw new IllegalStateException("accuracy not set or negative");
		if (this.cry == null)
			throw new IllegalStateException("cry not set");
		if (this.description == null)
			description = StringUtils.EMPTY;
		if (this.evasion < 0)
			throw new IllegalStateException("evasion not set or negative");
		if (this.experienceGroup == null)
			throw new IllegalStateException("experience group not set");
		if (this.imgBack == null)
			throw new IllegalStateException("img back not set");
		if (this.imgFront == null)
			throw new IllegalStateException("img front not set");
		if (this.magicalAttack < 0)
			throw new IllegalStateException("magical attack not set or negative");
		if (this.magicalDefense < 0)
			throw new IllegalStateException("magical defense not set or negative");
		if (this.maxHp < 0)
			throw new IllegalStateException("max hp not set or negative");
		if (this.maxMp < 0)
			throw new IllegalStateException("max mp not set or negative");
		if (this.name == null)
			throw new IllegalStateException("name is not set or negative");
		if (this.physicalAttack < 0)
			throw new IllegalStateException("physical attack not set or negative");
		if (this.physicalDefense < 0)
			throw new IllegalStateException("physical defense not set or negative");
		if (this.speed < 0)
			throw new IllegalStateException("speed not set or negative");		
		Character c = new Character();
		c.setAccuracy(accuracy);
		c.setCry(cry);
		c.setDescription(description);
		c.setElements(elements);
		c.setEvasion(evasion);
		c.setExperienceGroup(experienceGroup);
		c.setImgBack(imgBack);
		c.setImgFront(imgFront);
		c.setMagicalAttack(magicalAttack);
		c.setMagicalDefense(magicalDefense);
		c.setMaxHp(maxHp);
		c.setMaxMp(maxMp);
		c.setName(name);
		c.setPhysicalAttack(physicalAttack);
		c.setPhysicalDefense(physicalDefense);
		c.setSkills(skills);
		c.setSpeed(speed);
		return c;
	}

	public String getId() {
		return name;
	}

	public CharacterBuilder setAccuracy(int accuracy) {
		this.accuracy = accuracy;
		return this;
	}

	public CharacterBuilder setCry(String cry) {
		this.cry = cry;
		return this;
	}

	public CharacterBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	public CharacterBuilder setEvasion(int evasion) {
		this.evasion = evasion;
		return this;
	}

	public CharacterBuilder setExperienceGroup(EExperienceGroup experienceGroup) {
		this.experienceGroup = experienceGroup;
		return this;
	}

	public CharacterBuilder setImgBack(String imgBack) {
		this.imgBack = imgBack;
		return this;
	}

	public CharacterBuilder setImgFront(String imgFront) {
		this.imgFront = imgFront;
		return this;
	}

	public CharacterBuilder setMagicalAttack(int magicalAttack) {
		this.magicalAttack = magicalAttack;
		return this;
	}

	public CharacterBuilder setMagicalDefense(int magicalDefense) {
		this.magicalDefense = magicalDefense;
		return this;
	}

	public CharacterBuilder setMaxHp(int maxHp) {
		this.maxHp = maxHp;
		return this;
	}

	public CharacterBuilder setMaxMp(int maxMp) {
		this.maxMp = maxMp;
		return this;
	}

	public CharacterBuilder setName(String name) {
		if (name != null)
			throw new IllegalStateException("name cannot be changed");
		this.name = name;
		return this;
	}

	public CharacterBuilder setPhysicalAttack(int physicalAttack) {
		this.physicalAttack = physicalAttack;
		return this;
	}

	public CharacterBuilder setPhysicalDefense(int physicalDefense) {
		this.physicalDefense = physicalDefense;
		return this;
	}

	public CharacterBuilder setSpeed(int speed) {
		this.speed = speed;
		return this;
	}
}