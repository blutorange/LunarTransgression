package com.github.blutorange.translune.db;

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
import org.eclipse.jdt.annotation.Nullable;

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

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(@Nullable final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Item))
			return false;
		final Item other = (Item) obj;
		if (!name.equals(other.name))
			return false;
		return true;
	}

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

	/* (non-Javadoc)
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


}
