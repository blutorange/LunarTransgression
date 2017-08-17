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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.translune.logic.EItemEffect;

@Entity
@Table(name = "item")
public class Item extends AbstractEntity {
	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "effect", unique = false, nullable = false)
	private EItemEffect effect = EItemEffect.HEAL;

	@NotNull
	@Id
	@Column(name = "name", unique = true, nullable = false, length = 63)
	private String name = StringUtils.EMPTY;

	@Min(0)
	@Max(999)
	@Column(name = "power", nullable = false, unique = false)
	private int power;

	/**
	 * @return the effect
	 */
	public EItemEffect getEffect() {
		return effect;
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
	void setEffect(final EItemEffect effect) {
		this.effect = effect;
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
	public @NonNull String toString() {
		return String.format("Item(%s,%d,%s)", name, Integer.valueOf(power), effect);
	}

	@Override
	public Serializable getPrimaryKey() {
		return name;
	}

	@Override
	public EEntityMeta getEntityMeta() {
		return EEntityMeta.ITEM;
	}
}
