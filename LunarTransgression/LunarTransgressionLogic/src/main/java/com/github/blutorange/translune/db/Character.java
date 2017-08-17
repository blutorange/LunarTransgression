package com.github.blutorange.translune.db;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import com.github.blutorange.translune.logic.EElement;

@Entity
@Table(name = "\"character\"", uniqueConstraints = {@UniqueConstraint(columnNames="charstate", name = "uq_character_charstate")})
public class Character extends AbstractEntity {
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

	@NotNull
	@ManyToMany(targetEntity=Skill.class, cascade={CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinTable(name="character_skills", foreignKey=@ForeignKey(name="fk_ck_char"))
	private Set<Skill> skills;

	@Transient
	private Set<EElement> z_unmodifiableResistances = Collections.unmodifiableSet(resistances);

	@Transient
	private Set<EElement> z_unmodifiableWeaknesses = Collections.unmodifiableSet(weaknesses);

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

	protected Set<Skill> getSkills() {
		return skills;
	}

	protected void setSkills(final Set<Skill> skills) {
		this.skills = skills != null ? skills : new HashSet<>();
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

	@Override
	public Serializable getPrimaryKey() {
		return name;
	}

	@Override
	public EEntityMeta getEntityMeta() {
		return EEntityMeta.CHARACTER;
	}
}