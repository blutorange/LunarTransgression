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
import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.common.IAccessible;
import com.github.blutorange.translune.logic.EActionTarget;
import com.github.blutorange.translune.logic.EItemEffect;
import com.github.blutorange.translune.logic.ITargettable;
import com.github.blutorange.translune.util.Constants;
import com.jsoniter.annotation.JsonIgnore;

@Entity
@Table(name = "item")
public class Item extends AbstractStoredEntity implements ITargettable {
	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "effect", unique = false, nullable = false, updatable = false)
	private EItemEffect effect = EItemEffect.HEAL;

	@NonNull
	@NotNull
	@Id
	@Size(min=1,max=32)
	@Column(name = "name", unique = true, nullable = false, length = 32, updatable = false)
	private String name = StringUtils.EMPTY;

	@Min(0)
	@Max(Constants.MAX_ITEM_POWER)
	@Column(name = "power", nullable = false, unique = false, updatable = false)
	private int power;

	@Min(Constants.MIN_PRIORITY)
	@Max(Constants.MAX_PRIORITY)
	@Column(name = "priority", nullable = false, unique = false, updatable = false)
	private int priority = Constants.BATTLE_PRIORITY_ITEM;

	@NonNull
	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "target", nullable = false, unique = false, updatable = false)
	private EActionTarget target = EActionTarget.OPPONENTS_FIELD;

	public Item() {
	}

	@Override
	void forEachAssociatedObject(final Consumer<IAccessible<AbstractStoredEntity>> consumer) {
		// no associations
	}

	/**
	 * @return the effect
	 */
	public EItemEffect getEffect() {
		return effect;
	}

	@Override
	@JsonIgnore
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

	@NonNull
	@Override
	@JsonIgnore
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
	public EActionTarget getTarget() {
		return target;
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
	void setName(@NonNull final String name) {
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

	void setTarget(final EActionTarget target) {
		this.target = target != null ? target : EActionTarget.OPPONENTS_FIELD;
	}

	@Override
	public @NonNull String toString() {
		return String.format("Item(%s,%d,%s)", name, Integer.valueOf(power), effect);
	}
}
