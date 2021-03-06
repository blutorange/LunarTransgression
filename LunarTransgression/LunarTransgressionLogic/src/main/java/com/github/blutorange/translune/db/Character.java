package com.github.blutorange.translune.db;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.common.IAccessible;
import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.logic.EExperienceGroup;
import com.github.blutorange.translune.util.Constants;
import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;

@Entity
@Table(name = "\"character\"")
public class Character extends AbstractStoredEntity {
	@Min(0)
	@Max(Constants.MAX_ACCURACY)
	@Column(name = "accuracy", nullable = false, unique = false, updatable = false)
	private int accuracy;

	@NotEmpty
	@Column(name = "cry", nullable = false, unique = false, updatable = false)
	private String cry;

	@NotEmpty
	@Size(min = 1, max = 2048)
	@Column(name = "description", nullable = false, unique = false, updatable = false, length = 2048)
	private String description;

	@NonNull
	@NotNull
	@Column(name = "elements", nullable = false, unique = false, updatable = false)
	@ElementCollection(targetClass = EElement.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "charelements", joinColumns = @JoinColumn(name = "\"character\""), foreignKey = @ForeignKey(name = "fk_charelements_char"))
	private Set<@NonNull EElement> elements = EnumSet.noneOf(EElement.class);

	@Min(0)
	@Max(Constants.MAX_EVASION)
	@Column(name = "evasion", nullable = false, unique = false, updatable = false)
	private int evasion;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "experiencegroup", nullable = false, unique = false, updatable = false)
	private EExperienceGroup experienceGroup;

	@NotEmpty
	@Size(max = 64)
	@Column(name = "imgback", nullable = false, unique = false, updatable = false, length = 64)
	private String imgBack;

	@NotEmpty
	@Size(max = 64)
	@Column(name = "imgfront", nullable = false, unique = false, updatable = false, length = 64)
	private String imgFront;

	@NotEmpty
	@Size(max = 64)
	@Column(name = "imgicon", nullable = false, unique = false, updatable = false, length = 64)
	private String imgIcon;

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

	@NonNull
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
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "charskill", uniqueConstraints = @UniqueConstraint(name = "uk_charskill", columnNames = {
			"level", "skill",
			"\"character\"" }), foreignKey = @ForeignKey(name = "fk_charskill_char"), joinColumns = @JoinColumn(name = "\"character\"", nullable = false, unique = false, updatable = false, insertable = true))
	@MapKeyJoinColumn(name = "skill", foreignKey = @ForeignKey(name = "fk_charskill_skill"))
	@Column(name = "level", updatable = false, insertable = true, unique = false, nullable = false)
	private Map<Skill, Integer> skills;

	@Min(0)
	@Max(Constants.MAX_SPEED)
	@Column(name = "speed", nullable = false, unique = false, updatable = false)
	private int speed;

	public Character() {
	}

	@Override
	void forEachAssociatedObject(final Consumer<IAccessible<AbstractStoredEntity>> consumer) {
		final Set<Skill> set = new HashSet<>(skills.keySet());
		associated(set, consumer);
		for (final Skill skill : set) {
			final Integer level = skills.remove(skill);
			if (level != null) {
				skills.put(skill, level);
			}
		}
	}

	/**
	 * @return the accuracy
	 */
	public int getAccuracy() {
		return accuracy;
	}

	/**
	 * @return the cry
	 */
	public String getCry() {
		return cry;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the elements
	 */
	Set<EElement> getElements() {
		return elements;
	}

	@JsonIgnore
	@Override
	public EEntityMeta getEntityMeta() {
		return EEntityMeta.CHARACTER;
	}

	/**
	 * @return the evasion
	 */
	public int getEvasion() {
		return evasion;
	}

	/**
	 * @return the experienceGroup
	 */
	public EExperienceGroup getExperienceGroup() {
		return experienceGroup;
	}

	/**
	 * @return the gifBack
	 */
	public String getImgBack() {
		return imgBack;
	}

	/**
	 * @return the gif
	 */
	public String getImgFront() {
		return imgFront;
	}

	public String getImgIcon() {
		return imgIcon;
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

	@NonNull
	@Override
	@JsonIgnore
	public Serializable getPrimaryKey() {
		return name;
	}

	Map<Skill, Integer> getSkills() {
		return skills;
	}

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}

	@NonNull
	@JsonProperty(from="elements", to = "elements")
	public Set<@NonNull EElement> getUnmodifiableElements() {
		return elements;
	}

	@JsonProperty(value = "skills", from = "skills", to ="skills")
	@NonNull
	public Map<Skill, Integer> getUnmodifiableSkills() {
		return skills;
	}

	/**
	 * @param accuracy
	 *            the accuracy to set
	 */
	void setAccuracy(final int accuracy) {
		this.accuracy = accuracy;
	}

	/**
	 * @param cry
	 *            the cry to set
	 */
	void setCry(final String cry) {
		this.cry = cry;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @param elements
	 *            the elements to set
	 */
	void setElements(final Set<@NonNull EElement> elements) {
		this.elements = elements != null ? elements : EnumSet.noneOf(EElement.class);
	}

	/**
	 * @param evasion
	 *            the evasion to set
	 */
	void setEvasion(final int evasion) {
		this.evasion = evasion;
	}

	/**
	 * @param experienceGroup
	 *            the experienceGroup to set
	 */
	void setExperienceGroup(final EExperienceGroup experienceGroup) {
		this.experienceGroup = experienceGroup;
	}

	/**
	 * @param imgBack
	 *            the gifBack to set
	 */
	void setImgBack(final String imgBack) {
		this.imgBack = imgBack;
	}

	/**
	 * @param gif
	 *            the gif to set
	 */
	void setImgFront(final String imgFront) {
		this.imgFront = imgFront;
	}

	void setImgIcon(final String imgIcon) {
		this.imgIcon = imgIcon;
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
	void setName(@NonNull final String name) {
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

	void setSkills(final Map<Skill, Integer> skills) {
		this.skills = skills != null ? skills : new HashMap<>();
	}

	/**
	 * @param speed
	 *            the speed to set
	 */
	void setSpeed(final int speed) {
		this.speed = speed;
	}

	@SuppressWarnings("boxing")
	@Override
	public String toString() {
		return String.format("Character(%s,%s,hp=%d,mp=%s,patt=%d,pdef=%d,matt=%d,mdef=%s,speed=%s,acc=%d,ev=%d)", name,
				elements, maxHp, maxMp, physicalAttack, physicalDefense, magicalAttack, magicalDefense, speed,
				accuracy, evasion);
	}
}