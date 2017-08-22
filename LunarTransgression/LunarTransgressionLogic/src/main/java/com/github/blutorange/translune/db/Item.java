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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.common.IAccessible;
import com.github.blutorange.translune.logic.EItemEffect;
import com.github.blutorange.translune.util.Constants;

@Entity
@Table(name = "item")
public class Item extends AbstractStoredEntity {
	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "effect", unique = false, nullable = false, updatable = false)
	private EItemEffect effect = EItemEffect.HEAL;

	@NotNull
	@Id
	@Column(name = "name", unique = true, nullable = false, length = 63, updatable = false)
	private String name = StringUtils.EMPTY;

	@Min(0)
	@Max(Constants.MAX_ITEM_POWER)
	@Column(name = "power", nullable = false, unique = false, updatable = false)
	private int power;

	@Min(Constants.MIN_PRIORITY)
	@Max(Constants.MAX_PRIORITY)
	@Column(name = "priority", nullable = false, unique = false, updatable = false)
	private int priority = Constants.PRIORITY_ITEM;

	@Deprecated
	public Item() {
	}

	public Item(final String name, final EItemEffect effect, final int power) {
		this.name = name;
		this.effect = effect;
		this.power = power;
	}

	/**
	 * @return the effect
	 */
	public EItemEffect getEffect() {
		return effect;
	}

	@Override
	public EEntityMeta getEntityMeta() {
		return EEntityMeta.ITEM;
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

	@Override
	public @NonNull String toString() {
		return String.format("Item(%s,%d,%s)", name, Integer.valueOf(power), effect);
	}

	@Override
	void forEachAssociatedObject(final Consumer<IAccessible<AbstractStoredEntity>> consumer) {
		// no associations
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

	/**
	 * @param priority
	 *            the priority to set
	 */
	void setPriority(final int priority) {
		this.priority = priority;
	}
}
