package com.github.blutorange.translune.logic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.EStatusValue;
import com.jsoniter.annotation.JsonProperty;

public class CharacterStatsDelta {
	private EStatusValue value;
	private String before;
	private String after;
	private EStatusDeltaType type;
	private String characterState;

	public CharacterStatsDelta() {
		value = EStatusValue.HP;
		before = StringUtils.EMPTY;
		after = StringUtils.EMPTY;
		type = EStatusDeltaType.COMPUTED;
		characterState = StringUtils.EMPTY;
	}

	public CharacterStatsDelta(final String characterState, final EStatusValue value, final String before,
			final String after, final EStatusDeltaType type) {
		this.value = value;
		this.before = before;
		this.after = after;
		this.type = type;
		this.characterState = characterState;
	}

	/**
	 * @return the value
	 */
	@JsonProperty(required = true, nullable = false)
	public EStatusValue getValue() {
		return value;
	}

	/**
	 * @return the value before
	 */
	@JsonProperty(required = true)
	public String getBefore() {
		return before;
	}

	/**
	 * @return the value after
	 */
	@JsonProperty(required = true)
	public String getAfter() {
		return after;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(final EStatusValue value) {
		this.value = value;
	}

	/**
	 * @return the characterState
	 */
	@JsonProperty(required = true, nullable = false)
	public String getCharacterState() {
		return characterState;
	}

	/**
	 * @param characterState
	 *            the characterState to set
	 */
	public void setCharacterState(@Nullable final String characterState) {
		this.characterState = characterState != null ? characterState : StringUtils.EMPTY;
	}

	/**
	 * @param delta
	 *            the delta to set
	 */
	public void setBefore(@Nullable final String before) {
		this.before = before != null ? before : StringUtils.EMPTY;
	}

	public void setAfter(@Nullable final String after) {
		this.after = after != null ? after : StringUtils.EMPTY;
	}

	/**
	 * @return the isComputed
	 */
	@JsonProperty(required = true, nullable = false)
	public EStatusDeltaType getType() {
		return type;
	}

	/**
	 * @param isComputed
	 *            the isComputed to set
	 */
	public void setType(@Nullable final EStatusDeltaType type) {
		this.type = type != null ? type : EStatusDeltaType.COMPUTED;
	}

	public static List<CharacterStatsDelta> between(final String characterState, final IComputedStatus before,
			final IComputedStatus after) {
		final List<CharacterStatsDelta> list = new ArrayList<>();
		betweenComputed(characterState, list, before, after);
		return list;
	}

	public static List<CharacterStatsDelta> between(final String characterState, final IBattleStatus before,
			final IBattleStatus after) {
		final List<CharacterStatsDelta> list = new ArrayList<>();
		betweenBattleStatus(characterState, list, before, after);
		return list;
	}

	public static List<CharacterStatsDelta> between(final String characterState, final IComputedBattleStatus before,
			final IComputedBattleStatus after) {
		final List<CharacterStatsDelta> list = new ArrayList<>();
		betweenComputed(characterState, list, before, after);
		betweenBattleStatus(characterState, list, before.getBattleStatus(), after.getBattleStatus());
		betweenComputedBattle(characterState, list, before, after);
		return list;
	}

	private static void betweenBattleStatus(final String characterState, final List<CharacterStatsDelta> list,
			final IBattleStatus before, final IBattleStatus after) {
		addDiff(characterState, list, EStatusValue.ACCURACY, before.getStageAccuracy(), after.getStageAccuracy(),
				EStatusDeltaType.BATTLE_STATUS);
		addDiff(characterState, list, EStatusValue.HP, before.getHp(), after.getHp(),
				EStatusDeltaType.BATTLE_STATUS);
		addDiff(characterState, list, EStatusValue.MP, before.getMp(), after.getMp(),
				EStatusDeltaType.BATTLE_STATUS);
		addDiff(characterState, list, EStatusValue.STATUS_CONDITION, before.getStatusCondition(),
				after.getStatusCondition(), EStatusDeltaType.COMPUTED);
		addDiff(characterState, list, EStatusValue.EVASION, before.getStageEvasion(), after.getStageEvasion(),
				EStatusDeltaType.BATTLE_STATUS);
		addDiff(characterState, list, EStatusValue.MAGICAL_ATTACK, before.getStageMagicalAttack(),
				after.getStageMagicalAttack(), EStatusDeltaType.COMPUTED);
		addDiff(characterState, list, EStatusValue.MAGICAL_DEFENSE, before.getStageMagicalDefense(),
				after.getStageMagicalDefense(), EStatusDeltaType.COMPUTED);
		addDiff(characterState, list, EStatusValue.PHYSICAL_ATTACK, before.getStagePhysicalAttack(),
				after.getStagePhysicalAttack(), EStatusDeltaType.COMPUTED);
		addDiff(characterState, list, EStatusValue.PHYSICAL_DEFENSE, before.getStagePhysicalDefense(),
				after.getStagePhysicalDefense(), EStatusDeltaType.COMPUTED);
		addDiff(characterState, list, EStatusValue.SPEED, before.getStageSpeed(), after.getStageSpeed(),
				EStatusDeltaType.COMPUTED);
	}

	private static void betweenComputed(final String characterState, final List<CharacterStatsDelta> list,
			final IComputedStatus before, final IComputedStatus after) {
		addDiff(characterState, list, EStatusValue.ACCURACY, before.getComputedAccuracy(), after.getComputedAccuracy(),
				EStatusDeltaType.COMPUTED);
		addDiff(characterState, list, EStatusValue.EVASION, before.getComputedEvasion(), after.getComputedEvasion(),
				EStatusDeltaType.COMPUTED);
		addDiff(characterState, list, EStatusValue.HP, before.getComputedMaxHp(), after.getComputedMaxHp(),
				EStatusDeltaType.COMPUTED);
		addDiff(characterState, list, EStatusValue.MAGICAL_ATTACK, before.getComputedMagicalAttack(),
				after.getComputedMagicalAttack(), EStatusDeltaType.COMPUTED);
		addDiff(characterState, list, EStatusValue.MAGICAL_DEFENSE, before.getComputedMagicalDefense(),
				after.getComputedMagicalDefense(), EStatusDeltaType.COMPUTED);
		addDiff(characterState, list, EStatusValue.MP, before.getComputedMaxMp(), after.getComputedMaxMp(),
				EStatusDeltaType.COMPUTED);
		addDiff(characterState, list, EStatusValue.PHYSICAL_ATTACK, before.getComputedPhysicalAttack(),
				after.getComputedPhysicalAttack(), EStatusDeltaType.COMPUTED);
		addDiff(characterState, list, EStatusValue.PHYSICAL_DEFENSE, before.getComputedPhysicalDefense(),
				after.getComputedPhysicalDefense(), EStatusDeltaType.COMPUTED);
		addDiff(characterState, list, EStatusValue.SPEED, before.getComputedSpeed(), after.getComputedSpeed(),
				EStatusDeltaType.COMPUTED);
	}

	private static void betweenComputedBattle(final String characterState, final List<CharacterStatsDelta> list,
			final IComputedBattleStatus before, final IComputedBattleStatus after) {
		addDiff(characterState, list, EStatusValue.ACCURACY, before.getComputedBattleAccuracy(),
				after.getComputedBattleAccuracy(), EStatusDeltaType.COMPUTED_BATTLE);
		addDiff(characterState, list, EStatusValue.EVASION, before.getComputedBattleEvasion(),
				after.getComputedBattleEvasion(), EStatusDeltaType.COMPUTED_BATTLE);
		addDiff(characterState, list, EStatusValue.HP, before.getComputedBattleMaxHp(), after.getComputedBattleMaxHp(),
				EStatusDeltaType.COMPUTED_BATTLE);
		addDiff(characterState, list, EStatusValue.MAGICAL_ATTACK, before.getComputedBattleMagicalAttack(),
				after.getComputedBattleMagicalAttack(), EStatusDeltaType.COMPUTED_BATTLE);
		addDiff(characterState, list, EStatusValue.MAGICAL_DEFENSE, before.getComputedBattleMagicalDefense(),
				after.getComputedBattleMagicalDefense(), EStatusDeltaType.COMPUTED_BATTLE);
		addDiff(characterState, list, EStatusValue.MP, before.getComputedBattleMaxMp(), after.getComputedBattleMaxMp(),
				EStatusDeltaType.COMPUTED_BATTLE);
		addDiff(characterState, list, EStatusValue.PHYSICAL_ATTACK, before.getComputedBattlePhysicalAttack(),
				after.getComputedBattlePhysicalAttack(), EStatusDeltaType.COMPUTED_BATTLE);
		addDiff(characterState, list, EStatusValue.PHYSICAL_DEFENSE, before.getComputedBattlePhysicalDefense(),
				after.getComputedBattlePhysicalDefense(), EStatusDeltaType.COMPUTED_BATTLE);
		addDiff(characterState, list, EStatusValue.SPEED, before.getComputedBattleSpeed(),
				after.getComputedBattleSpeed(), EStatusDeltaType.COMPUTED_BATTLE);
	}

	private static void addDiff(final String characterState, final List<CharacterStatsDelta> list,
			final EStatusValue statusValue, final int before, final int after, final EStatusDeltaType type) {
		addDiff(characterState, list, statusValue, Integer.toString(before, 10), Integer.toString(after, 10), type);
	}

	private static void addDiff(final String characterState, final List<CharacterStatsDelta> list,
			final EStatusValue statusValue, @Nullable final Enum<?> before, @Nullable final Enum<?> after,
			final EStatusDeltaType type) {
		final String beforeString = before != null ? before.toString() : StringUtils.EMPTY;
		final String afterString = after != null ? after.toString() : StringUtils.EMPTY;
		addDiff(characterState, list, statusValue, beforeString, afterString, type);
	}

	private static void addDiff(final String characterState, final List<CharacterStatsDelta> list,
			final EStatusValue statusValue, @Nullable String before, @Nullable String after,
			final EStatusDeltaType type) {
		if (before == null)
			before = StringUtils.EMPTY;
		if (after == null)
			after = StringUtils.EMPTY;
		if (after.equals(before))
			return;
		list.add(new CharacterStatsDelta(characterState, statusValue, before, after, type));
	}
}