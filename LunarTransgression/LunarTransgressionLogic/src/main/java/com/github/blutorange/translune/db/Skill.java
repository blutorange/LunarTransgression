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

import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.logic.ESkillEffect;
import com.github.blutorange.translune.util.IAccessible;

@Entity
@Table(name = "skill")
public class Skill extends AbstractStoredEntity {
	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "effect", unique = false, nullable = false, updatable = false)
	private ESkillEffect effect = ESkillEffect.DAMAGE;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "element", nullable = false, unique = false, updatable = false)
	private EElement element = EElement.PHYSICAL;

	@Id
	@NotNull
	@Size(min = 1, max = 63)
	@Column(name = "name", nullable = false, unique = true, length = 63, updatable = false)
	private String name = StringUtils.EMPTY;

	@Min(0)
	@Max(999)
	@Column(name = "power", nullable = false, unique = false, updatable = false)
	private int power;

	@Min(1)
	@Max(20)
	@Column(name = "level", nullable = false, unique = false, updatable = false)
	private int level;

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

	public int getLevel() {
		return level;
	}

	void setLevel(final int level) {
		this.level = level;
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

	@Override
	public String toString() {
		return String.format("Skill(%s,%s,%d,%s)", name, element, Integer.valueOf(power), effect);
	}

	@Override
	public Serializable getPrimaryKey() {
		return name;
	}

	@Override
	public EEntityMeta getEntityMeta() {
		return EEntityMeta.SKILL;
	}

	@Override
	void forEachAssociatedObject(final Consumer<IAccessible<AbstractStoredEntity>> consumer) {
		// No associations
	}
}