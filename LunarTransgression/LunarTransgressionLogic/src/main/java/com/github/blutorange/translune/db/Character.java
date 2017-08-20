package com.github.blutorange.translune.db;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.util.Constants;
import com.github.blutorange.translune.util.IAccessible;

@Entity
@Table(name = "\"character\"")
public class Character extends AbstractStoredEntity {
	@NotNull
	@Column(name = "elements", nullable = false, unique = false, updatable = false)
	@ElementCollection(targetClass = EElement.class)
	@CollectionTable(name = "charelements", joinColumns = @JoinColumn(name = "\"character\""), foreignKey = @ForeignKey(name = "fk_charelements_char"))
	private Set<EElement> elements = EnumSet.of(EElement.NORMAL);

	@Min(0)
	@Max(Constants.MAX_MAGICAL_ATTACK)
	@Column(name = "magicalattack", nullable = false, unique = false, updatable = false)
	private int magicalAttack;

	@Min(0)
	@Max(Constants.MAX_MAGICAL_DEFENSE)
	@Column(name = "magicaldefense", nullable = false, unique = false, updatable = false)
	private int magicalDefense;

	@Min(0)
	@Max(Constants.MAX_HP)
	@Column(name = "maxhp", nullable = false, unique = false, updatable = false)
	private int maxHp;

	@Min(0)
	@Max(Constants.MAX_MP)
	@Column(name = "maxmp", nullable = false, unique = false, updatable = false)
	private int maxMp;

	@Id
	@NotNull
	@Size(min = 1, max = 63)
	@Column(name = "name", nullable = false, unique = true, length = 63, updatable = false)
	private String name = StringUtils.EMPTY;

	@Min(0)
	@Max(Constants.MAX_PHYSICAL_ATTACK)
	@Column(name = "physicalattack", nullable = false, unique = false, updatable = false)
	private int physicalAttack;

	@Min(0)
	@Max(Constants.MAX_PHYSICAL_DEFENSE)
	@Column(name = "physicaldefense", nullable = false, unique = false, updatable = false)
	private int physicalDefense;

	@NotNull
	@ManyToMany(targetEntity = Skill.class, cascade = {}, fetch = FetchType.LAZY)
	@JoinTable(name = "charskill", joinColumns = @JoinColumn(name = "charskill_char", updatable = false, nullable = false, foreignKey = @ForeignKey(name="fk_charskill_char")), inverseJoinColumns = @JoinColumn(name = "charskill_skill", updatable = false, nullable = false, foreignKey = @ForeignKey(name="fk_charskill_skill")))
	@MapKeyColumn(name = "level", updatable = false, nullable = false, unique = false)
	private Map<Integer, Skill> skills;

	@Min(0)
	@Max(Constants.MAX_SPEED)
	@Column(name = "speed", nullable = false, unique = false, updatable = false)
	private int speed;

	/**
	 * @return the elements
	 */
	Set<EElement> getElements() {
		return elements;
	}

	public Set<EElement> getUnmodifiableElements() {
		return elements;
	}

	@Override
	public EEntityMeta getEntityMeta() {
		return EEntityMeta.CHARACTER;
	}

	/**
	 * @return the magicalAttack
	 */
	public int getMagicalAttack() {
		return magicalAttack;
	}

	/**
	 * @return the magicalDefense
	 */
	public int getMagicalDefense() {
		return magicalDefense;
	}

	/**
	 * @return the maxHp
	 */
	public int getMaxHp() {
		return maxHp;
	}

	/**
	 * @return the maxMp
	 */
	public int getMaxMp() {
		return maxMp;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the physicalAttack
	 */
	public int getPhysicalAttack() {
		return physicalAttack;
	}

	/**
	 * @return the physicalDefense
	 */
	public int getPhysicalDefense() {
		return physicalDefense;
	}

	@Override
	public Serializable getPrimaryKey() {
		return name;
	}

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}

	public Map<Integer, Skill> getUnmodifiableSkills() {
		return skills;
	}

	/**
	 * @param elements
	 *            the elements to set
	 */
	void setElements(final Set<EElement> elements) {
		this.elements = elements != null ? elements : EnumSet.noneOf(EElement.class);
	}

	@Override
	public String toString() {
		return String.format("Character(%s,hp=%d,mp=%s,patt=%d,pdef=%d,matt=%d,mdef=%s,speed=%s)", name, maxHp, maxMp,
				physicalAttack, physicalDefense, magicalAttack, magicalDefense, speed);
	}

	@Override
	void forEachAssociatedObject(final Consumer<IAccessible<AbstractStoredEntity>> consumer) {
		associated(skills.values(), consumer);
	}

	Map<Integer, Skill> getSkills() {
		return skills;
	}

	/**
	 * @param magicalAttack
	 *            the magicalAttack to set
	 */
	void setMagicalAttack(final int magicalAttack) {
		this.magicalAttack = magicalAttack;
	}

	/**
	 * @param magicalDefense
	 *            the magicalDefense to set
	 */
	void setMagicalDefense(final int magicalDefense) {
		this.magicalDefense = magicalDefense;
	}

	/**
	 * @param maxHp
	 *            the maxHp to set
	 */
	void setMaxHp(final int maxHp) {
		this.maxHp = maxHp;
	}

	/**
	 * @param maxMp
	 *            the maxMp to set
	 */
	void setMaxMp(final int maxMp) {
		this.maxMp = maxMp;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	void setName(final String name) {
		this.name = name;
	}

	/**
	 * @param physicalAttack
	 *            the physicalAttack to set
	 */
	void setPhysicalAttack(final int physicalAttack) {
		this.physicalAttack = physicalAttack;
	}

	/**
	 * @param physicalDefense
	 *            the physicalDefense to set
	 */
	void setPhysicalDefense(final int physicalDefense) {
		this.physicalDefense = physicalDefense;
	}

	void setSkills(final Map<Integer, Skill> skills) {
		this.skills = skills != null ? skills : new HashMap<>();
	}

	/**
	 * @param speed
	 *            the speed to set
	 */
	void setSpeed(final int speed) {
		this.speed = speed;
	}
}