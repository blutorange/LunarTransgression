package com.github.blutorange.translune.db;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.Builder;

import com.github.blutorange.translune.logic.EActionTarget;
import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.logic.ESkillEffect;

public class SkillBuilder implements Builder<Skill> {

	private int accuracy = 100;
	private String description = StringUtils.EMPTY;
	private ESkillEffect effect;
	private EElement element;
	private boolean highCritical = false;
	private boolean isPhysical = true;
	private int mp = -1;
	private String name;
	private int power = -1;
	private int priority = 0;
	private EActionTarget target;

	public SkillBuilder() {

	}

	public Skill build() {
		if (element == null)
			throw new IllegalStateException("element is null");
		if (effect == null)
			throw new IllegalStateException("effect is null");
		if (target == null)
			throw new IllegalStateException("target is null");
		if (name == null)
			throw new IllegalStateException("name is null");
		if (power < 0)
			throw new IllegalStateException("power is not set or negative");
		if (mp < 0)
			throw new IllegalStateException("mp is not set or negative");
		Skill skill = new Skill();
		skill.setAccuracy(accuracy);
		skill.setDescription(description);
		skill.setEffect(effect);
		skill.setElement(element);
		skill.setHighCritical(highCritical);
		skill.setIsPhysical(isPhysical);
		skill.setMp(mp);
		skill.setName(name);
		skill.setPower(power);
		skill.setPriority(priority);
		skill.setTarget(target);
		return skill;
	}

	public SkillBuilder setAccuracy(int accuracy) {
		this.accuracy = accuracy;
		return this;
	}

	public SkillBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	public SkillBuilder setEffect(ESkillEffect effect) {
		this.effect = effect;
		return this;
	}

	public SkillBuilder setElement(EElement element) {
		this.element = element;
		return this;
	}

	public SkillBuilder setHighCritical(boolean highCritical) {
		this.highCritical = highCritical;
		return this;
	}

	public SkillBuilder setMp(int mp) {
		this.mp = mp;
		return this;
	}
	
	public String getId() {
		return name;
	}

	public SkillBuilder setName(String name) {
		if (name != null)
			throw new IllegalStateException("name cannot be changed");
		this.name = name;
		return this;
	}

	public SkillBuilder setPhysical(boolean isPhysical) {
		this.isPhysical = isPhysical;
		return this;
	}

	public SkillBuilder setPower(int power) {
		this.power = power;
		return this;
	}

	public SkillBuilder setPriority(int priority) {
		this.priority = priority;
		return this;
	}

	public SkillBuilder setTarget(EActionTarget target) {
		this.target = target;
		return this;
	}
}
