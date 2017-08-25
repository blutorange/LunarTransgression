package com.github.blutorange.translune.db;

import java.io.Serializable;
import java.util.function.Consumer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import com.github.blutorange.common.IAccessible;
import com.github.blutorange.translune.logic.EActionTarget;
import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.logic.ESkillEffect;
import com.github.blutorange.translune.logic.ITargettable;
import com.github.blutorange.translune.util.Constants;

@Entity
@Table(name = "skill")
public class Skill extends AbstractStoredEntity implements ITargettable {
	@Min(0)
	@Max(Constants.MAX_ACCURACY)
	@Column(name = "accuracy", nullable = false, unique = false, updatable = false)
	private int accuracy;

	@NotNull
	@Size(min = 1, max = 2048)
	@Column(name = "description", nullable = false, unique = false, length = 2048, updatable = false)
	private String description = StringUtils.EMPTY;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "effect", unique = false, nullable = false, updatable = false)
	private ESkillEffect effect = ESkillEffect.PHYSICAL_DAMAGE;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "element", nullable = false, unique = false, updatable = false)
	private EElement element = EElement.PHYSICAL;

	@Column(name = "highcritical", nullable = false, unique = false, updatable = false)
	private boolean highCritical;

	@Column(name = "isphysical", nullable = false, unique = false, updatable = false)
	private boolean isPhysical;

	/**
	 * The MP this skill consumes. May be 0.
	 */
	@Min(0)
	@Max(Constants.MAX_MP)
	@Column(name = "mp", nullable = false, unique = false, updatable = false)
	private int mp;

	@Id
	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "name", nullable = false, unique = true, length = 32, updatable = false)
	private String name = StringUtils.EMPTY;

	@Min(0)
	@Max(Constants.MAX_SKILL_POWER)
	@Column(name = "power", nullable = false, unique = false, updatable = false)
	private int power;

	@Min(Constants.MIN_PRIORITY)
	@Max(Constants.MAX_PRIORITY)
	@Column(name = "priority", nullable = false, unique = false, updatable = false)
	private int priority;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "target", nullable = false, unique = false, updatable = false)
	private EActionTarget target = EActionTarget.OPPONENTS_FIELD;

	@Override
	void forEachAssociatedObject(final Consumer<IAccessible<AbstractStoredEntity>> consumer) {
		// No associations
	}

	/**
	 * @return the accuracy
	 */
	public int getAccuracy() {
		return accuracy;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * @return the effect
	 */
	public ESkillEffect getEffect() {
		return effect;
	}

	/**
	 * @return the element
	 */
	public EElement getElement() {
		return element;
	}

	@Override
	public EEntityMeta getEntityMeta() {
		return EEntityMeta.SKILL;
	}

	/**
	 * @return the highCritical
	 */
	public boolean getHighCritical() {
		return highCritical;
	}

	/**
	 * @return the isPhysical
	 */
	public boolean getIsPhysical() {
		return isPhysical;
	}

	/**
	 * @return the mp
	 */
	public int getMp() {
		return mp;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the power
	 */
	public int getPower() {
		return power;
	}

	@Override
	public Serializable getPrimaryKey() {
		return name;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @return the target
	 */
	@Override
	public EActionTarget getTarget() {
		return target;
	}

	/**
	 * @param accuracy
	 *            the accuracy to set
	 */
	void setAccuracy(final int accuracy) {
		this.accuracy = accuracy;
	}

	void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @param effect
	 *            the effect to set
	 */
	void setEffect(final ESkillEffect effect) {
		this.effect = effect;
	}

	/**
	 * @param element
	 *            the element to set
	 */
	void setElement(final EElement element) {
		this.element = element;
	}

	/**
	 * @param highCritical
	 *            the highCritical to set
	 */
	void setHighCritical(final boolean highCritical) {
		this.highCritical = highCritical;
	}

	/**
	 * @param isPhysical
	 *            the isPhysical to set
	 */
	void setIsPhysical(final boolean isPhysical) {
		this.isPhysical = isPhysical;
	}

	/**
	 * @param mp
	 *            the mp to set
	 */
	void setMp(final int mp) {
		this.mp = mp;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	void setName(final String name) {
		this.name = name;
	}

	/**
	 * @param power
	 *            the power to set
	 */
	void setPower(final int power) {
		this.power = power;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	void setPriority(final int priority) {
		this.priority = priority;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	void setTarget(final EActionTarget target) {
		this.target = target;
	}

	@Override
	public String toString() {
		return String.format("Skill(%s,%s,%d,%s)", name, element, Integer.valueOf(power), effect);
	}
}