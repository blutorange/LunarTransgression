package com.github.blutorange.translune.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.common.IAccessible;
import com.github.blutorange.translune.logic.EActionTarget;
import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.logic.ESkillEffect;
import com.github.blutorange.translune.logic.EStatusCondition;
import com.github.blutorange.translune.logic.IAccuracied;
import com.github.blutorange.translune.logic.IFlinched;
import com.github.blutorange.translune.logic.ISkilled;
import com.github.blutorange.translune.logic.IStaged;
import com.github.blutorange.translune.logic.IStatusConditioned;
import com.github.blutorange.translune.logic.ITargettable;
import com.github.blutorange.translune.util.Constants;

@Entity
@Table(name = "skill")
public class Skill extends AbstractStoredEntity implements ITargettable, ISkilled, IAccuracied, IStatusConditioned, IFlinched, IStaged {
	@Min(0)
	@Max(Constants.MAX_ACCURACY)
	@Column(name = "accuracy", nullable = false, unique = false, updatable = false)
	private int accuracy;

	@Column(name = "alwayshits", nullable = false, unique = false, updatable = false)
	private boolean alwaysHits;

	@Min(0)
	@Max(Constants.MAX_SKILL_POWER)
	@Column(name = "attackpower", nullable = false, unique = false, updatable = false)
	private int attackPower;

	@Nullable
	@Enumerated(value = EnumType.STRING)
	@Column(name = "condition", nullable = true, unique = false, updatable = false, insertable = true)
	private EStatusCondition condition;

	@Min(0)
	@Max(100)
	@Column(name = "conditionchance", nullable = false, unique = false, updatable = false)
	private int conditionChance;

	@NotNull
	@Size(min = 1, max = 2048)
	@Column(name = "description", nullable = false, unique = false, length = 2048, updatable = false)
	private String description = StringUtils.EMPTY;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "effect", unique = false, nullable = false, updatable = false)
	private ESkillEffect effect = ESkillEffect.NONE;

	@NonNull
	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "element", nullable = false, unique = false, updatable = false)
	private EElement element = EElement.NORMAL;

	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "flavor", nullable = false, unique = false, length = 32, updatable = false)
	private String flavor = StringUtils.EMPTY;

	@Min(0)
	@Max(100)
	@Column(name = "flinchchance", nullable = false, unique = false, updatable = false)
	private int flinchChance;

	@Min(0)
	@Max(Constants.MAX_SKILL_POWER)
	@Column(name = "healpower", nullable = false, unique = false, updatable = false)
	private int healPower;

	@Column(name = "highcritical", nullable = false, unique = false, updatable = false)
	private boolean highCritical;

	@Column(name = "isphysical", nullable = false, unique = false, updatable = false)
	private boolean isPhysical;

	/**
	 * The MP this skill consumes. May be 0.
	 */
	@Min(0)
	@Max(Constants.MAX_MP)
	@Column(name = "mp", nullable = false, unique = false, updatable = false)
	private int mp;

	@NonNull
	@Id
	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "name", nullable = false, unique = true, length = 32, updatable = false)
	private String name = StringUtils.EMPTY;

	@Min(Constants.MIN_PRIORITY)
	@Max(Constants.MAX_PRIORITY)
	@Column(name = "priority", nullable = false, unique = false, updatable = false)
	private int priority;

	@Min(0)
	@Max(100)
	@Column(name = "stagechance", nullable = false, unique = false, updatable = false)
	private int stageChance;

	@NotNull
	@ElementCollection
	@CollectionTable(name = "skillstagechange", uniqueConstraints = @UniqueConstraint(name = "uk_skillstagechange", columnNames = {
			"stagepower", "statusvalue",
			"skill" }), foreignKey = @ForeignKey(name = "fk_skillstagechange_skill"), joinColumns = @JoinColumn(name = "skill", nullable = false, unique = false, updatable = false, insertable = true))
	@MapKeyJoinColumn(name = "skill", foreignKey = @ForeignKey(name = "fk_charskill_skill"))
	@Column(name = "level", updatable = false, insertable = true, unique = false, nullable = false)
	private Map<EStatusValue, Integer> stageChanges;

	@NonNull
	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "target", nullable = false, unique = false, updatable = false)
	private EActionTarget target = EActionTarget.OPPONENTS_FIELD;

	Skill() {
	}

	/**
	 * @return the accuracy
	 */
	@Override
	public int getAccuracy() {
		return accuracy;
	}

	/**
	 * @return the alwaysHits
	 */
	@Override
	public boolean getAlwaysHits() {
		return alwaysHits;
	}

	/**
	 * @return the power
	 */
	@Override
	public int getAttackPower() {
		return attackPower;
	}

	/**
	 * @return the condition
	 */
	@Override
	@Nullable
	public EStatusCondition getCondition() {
		return condition;
	}

	/**
	 * @return the conditionPower
	 */
	@Override
	public int getConditionChance() {
		return conditionChance;
	}

	public String getDescription() {
		return description;
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
	@Override
	public EElement getElement() {
		return element;
	}

	@Override
	public EEntityMeta getEntityMeta() {
		return EEntityMeta.SKILL;
	}

	/**
	 * @return the flavor
	 */
	public String getFlavor() {
		return flavor;
	}

	/**
	 * @return the flinchChance
	 */
	@Override
	public int getFlinchChance() {
		return flinchChance;
	}

	/**
	 * @return the healPower
	 */
	public int getHealPower() {
		return healPower;
	}

	/**
	 * @return the highCritical
	 */
	@Override
	public boolean getHighCritical() {
		return highCritical;
	}

	/**
	 * @return the isPhysical
	 */
	@Override
	public boolean getIsPhysical() {
		return isPhysical;
	}

	/**
	 * @return the mp
	 */
	public int getMp() {
		return mp;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	@NonNull
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

	/**
	 * @return the stageChance
	 */
	public int getStageChance() {
		return stageChance;
	}

	/**
	 * @return the stagePower
	 */
	@Override
	public Map<EStatusValue, Integer> getStageChanges() {
		return stageChanges;
	}

	/**
	 * @return the target
	 */
	@Override
	public EActionTarget getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return String.format("Skill(%s,%s,%d,%s)", name, element, Integer.valueOf(attackPower), effect);
	}

	@Override
	void forEachAssociatedObject(final Consumer<IAccessible<AbstractStoredEntity>> consumer) {
		// No associations
	}

	/**
	 * @param accuracy
	 *            the accuracy to set
	 */
	void setAccuracy(final int accuracy) {
		this.accuracy = accuracy;
	}

	/**
	 * @param alwaysHits
	 *            the alwaysHits to set
	 */
	void setAlwaysHits(final boolean alwaysHits) {
		this.alwaysHits = alwaysHits;
	}

	/**
	 * @param power
	 *            the power to set
	 */
	void setAttackPower(final int power) {
		this.attackPower = power;
	}

	/**
	 * @param condition
	 *            the condition to set
	 */
	void setCondition(@Nullable final EStatusCondition condition) {
		this.condition = condition;
	}

	/**
	 * @param conditionPower
	 *            the conditionPower to set
	 */
	void setConditionChance(final int conditionChance) {
		this.conditionChance = conditionChance;
	}

	void setDescription(final String description) {
		this.description = description;
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
	void setElement(@Nullable final EElement element) {
		this.element = element != null ? element : EElement.NORMAL;
	}

	/**
	 * @param flavor
	 *            the flavor to set
	 */
	void setFlavor(final String flavor) {
		this.flavor = flavor;
	}

	/**
	 * @param flinchChance
	 *            the flinchChance to set
	 */
	void setFlinchChance(final int flinchChance) {
		this.flinchChance = flinchChance;
	}

	/**
	 * @param healPower
	 *            the healPower to set
	 */
	void setHealPower(final int healPower) {
		this.healPower = healPower;
	}

	/**
	 * @param highCritical
	 *            the highCritical to set
	 */
	void setHighCritical(final boolean highCritical) {
		this.highCritical = highCritical;
	}

	/**
	 * @param isPhysical
	 *            the isPhysical to set
	 */
	void setIsPhysical(final boolean isPhysical) {
		this.isPhysical = isPhysical;
	}

	/**
	 * @param mp
	 *            the mp to set
	 */
	void setMp(final int mp) {
		this.mp = mp;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	void setName(@NonNull final String name) {
		this.name = name;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	void setPriority(final int priority) {
		this.priority = priority;
	}

	/**
	 * @param stageChance
	 *            the stageChance to set
	 */
	void setStageChance(final int stageChance) {
		this.stageChance = stageChance;
	}

	/**
	 * @param stagePower
	 *            the stagePower to set
	 */
	void setStageChanges(final Map<EStatusValue, Integer> stageChanges) {
		this.stageChanges = stageChanges != null ? stageChanges : new HashMap<>();
	}

	/**
	 * @param target
	 *            the target to set
	 */
	void setTarget(@Nullable final EActionTarget target) {
		this.target = target != null ? target : EActionTarget.OPPONENTS_FIELD;
	}
}