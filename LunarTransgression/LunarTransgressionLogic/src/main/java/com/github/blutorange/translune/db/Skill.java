package com.github.blutorange.translune.db;

import java.io.Serializable;

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
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.logic.ESkillEffect;

@Entity
@Table(name = "skill")
public class Skill extends AbstractEntity {
	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "effect", unique = false, nullable = false)
	private ESkillEffect effect = ESkillEffect.DAMAGE;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "element", nullable = false, unique = false)
	private EElement element = EElement.PHYSICAL;

	@Id
	@NotNull
	@Size(min = 1, max = 63)
	@Column(name = "name", nullable = false, unique = true, length = 63)
	private String name = StringUtils.EMPTY;

	@Min(0)
	@Max(999)
	@Column(name = "power", nullable = false, unique = false)
	private int power;

	@Min(1)
	@Max(20)
	@Column(name = "level", nullable = false, unique = false)
	private int level;
	
	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(@Nullable final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Skill))
			return false;
		final Skill other = (Skill) obj;
		if (!name.equals(other.name))
			return false;
		return true;
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
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
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

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + name.hashCode();
		return result;
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
}