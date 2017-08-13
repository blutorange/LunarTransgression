package com.github.blutorange.translune.db;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.logic.EElement;

@Entity
@Table(name = "\"character\"", uniqueConstraints = {@UniqueConstraint(columnNames="charstate", name = "uq_character_charstate")})
public class Character extends AbstractEntity {

	@NotNull
	@OneToOne(targetEntity = CharacterState.class, cascade = CascadeType.ALL, orphanRemoval = true, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "charstate", updatable = false, insertable = true, nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_player_cstat"))
	private CharacterState characterState = new CharacterState();

	@Id
	@NotNull
	@Size(min = 1, max = 63)
	@Column(name = "name", nullable = false, unique = true, length = 63)
	private String name = StringUtils.EMPTY;

	@NotNull
	@Column(name = "resistance", nullable = false, unique = false)
	@ElementCollection(targetClass = EElement.class)
	@CollectionTable(name = "resistances", joinColumns = @JoinColumn(name = "\"character\""), foreignKey = @ForeignKey(name="fk_char_resist"))
	private Set<EElement> resistances = EnumSet.noneOf(EElement.class);

	@NotNull
	@Column(name = "weakness", nullable = false, unique = false)
	@ElementCollection(targetClass = EElement.class)
	@CollectionTable(name = "weaknesses", joinColumns = @JoinColumn(name = "\"character\""), foreignKey = @ForeignKey(name="fk_char_weakness"))
	private Set<EElement> weaknesses = EnumSet.noneOf(EElement.class);

	@Transient
	private Set<EElement> z_unmodifiableResistances = Collections.unmodifiableSet(resistances);

	@Transient
	private Set<EElement> z_unmodifiableWeaknesses = Collections.unmodifiableSet(weaknesses);

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final @Nullable Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Character))
			return false;
		final Character other = (Character) obj;
		if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * @return the stats
	 */
	public CharacterState getCharacterState() {
		return characterState;
	}

	/**
	 * @return the resistances
	 */
	public Set<EElement> getUnmodifiableResistances() {
		return z_unmodifiableResistances;
	}

	/**
	 * @return the weaknesses
	 */
	public Set<EElement> getUnmodifiableWeaknesses() {
		return Collections.unmodifiableSet(weaknesses);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the resistances
	 */
	Set<EElement> getResistances() {
		return resistances;
	}

	/**
	 * @return the weaknesses
	 */
	Set<EElement> getWeaknesses() {
		return weaknesses;
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
	 * @param stats
	 *            the stats to set
	 */
	void setCharacterState(final CharacterState stats) {
		this.characterState = stats;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	void setName(final String name) {
		this.name = name;
	}

	/**
	 * @param resistances
	 *            the resistances to set
	 */
	void setResistances(final Set<EElement> resistances) {
		this.resistances = resistances;
		this.z_unmodifiableResistances = Collections.unmodifiableSet(this.resistances);
	}

	/**
	 * @param weaknesses
	 *            the weaknesses to set
	 */
	void setWeaknesses(final Set<EElement> weaknesses) {
		this.weaknesses = weaknesses;
		this.z_unmodifiableWeaknesses = Collections.unmodifiableSet(this.weaknesses);
	}

	@Override
	public String toString() {
		return String.format("Character(%s)", name);
	}
}