package com.github.blutorange.translune.db;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.Builder;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.logic.EActionTarget;
import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.logic.ESkillEffect;
import com.github.blutorange.translune.logic.EStatusCondition;

public class SkillBuilder implements Builder<Skill> {

	private int accuracy = 100;
	private boolean alwaysHits;
	private int attackPower = -1;
	@Nullable
	private EStatusCondition condition;
	private int conditionChance = 0;
	private String description = StringUtils.EMPTY;
	private ESkillEffect effect;
	private EElement element;
	private String flavor = StringUtils.EMPTY;
	private int flinchChance = 0;
	private int healPower = 0;
	private boolean highCritical = false;
	private boolean isPhysical = true;
	private int mp = -1;
	private String name;
	private int priority = 0;
	private int stageChance = 0;
	private final Map<EStatusValue, Integer> stageChanges = new HashMap<>();
	private EActionTarget target;

	public SkillBuilder() {

	}

	public void addStageChange(final EStatusValue statusValue, final int stagePower) {
		stageChanges.put(statusValue, Integer.valueOf(stagePower));
	}

	public void addStageChanges(final Map<EStatusValue, Integer> stageChanges) {
		this.stageChanges.putAll(stageChanges);
	}

	@Override
	public Skill build() {
		final String name = this.name;
		if (element == null)
			throw new IllegalStateException("element is null");
		if (effect == null)
			throw new IllegalStateException("effect is null");
		if (target == null)
			throw new IllegalStateException("target is null");
		if (name == null)
			throw new IllegalStateException("name is null");
		if (attackPower < 0)
			throw new IllegalStateException("attack power is not set or negative");
		if (healPower < 0)
			throw new IllegalStateException("heal power is negative");
		if (conditionChance < 0)
			throw new IllegalStateException("condition chance is negative");
		if (stageChance < 0)
			throw new IllegalStateException("stage chance is negative");
		if (flinchChance < 0)
			throw new IllegalStateException("flinch chance is negative");
		if (mp < 0)
			throw new IllegalStateException("mp is not set or negative");
		if (flavor == null)
			flavor = StringUtils.EMPTY;
		final Skill skill = new Skill();
		skill.setAccuracy(accuracy);
		skill.setAlwaysHits(alwaysHits);
		skill.setAttackPower(attackPower);
		skill.setCondition(condition);
		skill.setConditionChance(conditionChance);
		skill.setDescription(description);
		skill.setEffect(effect);
		skill.setElement(element);
		skill.setFlinchChance(flinchChance);
		skill.setFlavor(flavor);
		skill.setHealPower(healPower);
		skill.setHighCritical(highCritical);
		skill.setIsPhysical(isPhysical);
		skill.setMp(mp);
		skill.setName(name);
		skill.setStageChance(stageChance);
		skill.setStageChanges(stageChanges);
		skill.setPriority(priority);
		skill.setTarget(target);
		return skill;
	}

	public void clearStageChanges() {
		stageChanges.clear();
	}

	public String getId() {
		return name;
	}

	public void removeStageChange(final EStatusValue statusValue) {
		stageChanges.remove(statusValue);
	}

	public SkillBuilder setAccuracy(final int accuracy) {
		this.accuracy = accuracy;
		return this;
	}

	public SkillBuilder setAlwaysHits(final boolean alwaysHits) {
		this.alwaysHits = alwaysHits;
		return this;
	}

	public SkillBuilder setAttackPower(final int attackPower) {
		this.attackPower = attackPower;
		return this;
	}

	public SkillBuilder setCondition(@Nullable final EStatusCondition condition) {
		this.condition = condition;
		return this;
	}

	public SkillBuilder setConditionChance(final int conditionChance) {
		this.conditionChance = conditionChance;
		return this;
	}

	public SkillBuilder setDescription(final String description) {
		this.description = description;
		return this;
	}

	public SkillBuilder setEffect(final ESkillEffect effect) {
		this.effect = effect;
		return this;
	}

	public SkillBuilder setElement(final EElement element) {
		this.element = element;
		return this;
	}

	public SkillBuilder setFlavor(final String flavor) {
		this.flavor = flavor;
		return this;
	}

	public SkillBuilder setFlinchChance(final int flinchChance) {
		this.flinchChance = flinchChance;
		return this;
	}

	public SkillBuilder setHealPower(final int healPower) {
		this.healPower = healPower;
		return this;
	}

	public SkillBuilder setHighCritical(final boolean highCritical) {
		this.highCritical = highCritical;
		return this;
	}

	public SkillBuilder setIsPhysical(final boolean isPhysical) {
		this.isPhysical = isPhysical;
		return this;
	}

	public SkillBuilder setMp(final int mp) {
		this.mp = mp;
		return this;
	}

	public SkillBuilder setName(final String name) {
		if (this.name != null)
			throw new IllegalStateException("player name cannot be changed: " + this.name);
		this.name = name;
		return this;
	}

	public SkillBuilder setPriority(final int priority) {
		this.priority = priority;
		return this;
	}

	public SkillBuilder setStageChance(final int stageChance) {
		this.stageChance = stageChance;
		return this;
	}

	public SkillBuilder setTarget(final EActionTarget target) {
		this.target = target;
		return this;
	}
}
