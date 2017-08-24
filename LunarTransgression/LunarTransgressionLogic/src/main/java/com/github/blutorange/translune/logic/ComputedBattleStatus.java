package com.github.blutorange.translune.logic;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.common.IntToIntFunction;
import com.github.blutorange.common.MathUtil;
import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.util.Constants;

class ComputedBattleStatus extends ComputedStatus implements IComputedBattleStatus {
	private final static int[] stageMultiplierAccEvDenominator = new int[] { 9, 8, 7, 6, 5, 4, 3, 3, 3, 3, 3, 3, 3 };

	private final static int[] stageMultiplierAccEvNumerator = new int[] { 3, 3, 3, 3, 3, 3, 3, 4, 5, 6, 7, 8, 9 };

	private final static int[] stageMultiplierDenominator = new int[] { 8, 7, 6, 5, 4, 3, 2, 2, 2, 2, 2, 2, 2 };

	private final static int[] stageMultiplierNumerator = new int[] { 2, 2, 2, 2, 2, 2, 2, 3, 4, 5, 6, 7, 8 };

	private final BattleStatus battleStatus;

	public ComputedBattleStatus(final CharacterState characterState, final BattleStatus battleStatus) {
		super(characterState);
		this.battleStatus = battleStatus;
	}

	@Override
	public BattleStatus getBattleStatus() {
		return battleStatus;
	}

	@Override
	public int getComputedBattleAccuracy() {
		return computedBattleAccuracy();
	}

	@Override
	public int getComputedBattleEvasion() {
		return computedBattleEvasion();
	}

	@Override
	public int getComputedBattleHpAbsolute() {
		return battleStatus.getHp() * getComputedBattleMaxHp() / Constants.MAX_RELATIVE_HP;
	}

	@Override
	public int getComputedBattleMagicalAttack() {
		return computedBattleMagicalAttack();
	}

	@Override
	public int getComputedBattleMagicalDefense() {
		return computedBattleMagicalDefense();
	}

	@Override
	public int getComputedBattleMaxHp() {
		return computedBattleMaxHp();
	}

	@Override
	public int getComputedBattleMaxMp() {
		return computedBattleMaxMp();
	}

	@Override
	public int getComputedBattleMpAbsolute() {
		return battleStatus.getMp() * getComputedBattleMaxMp() / Constants.MAX_RELATIVE_MP;
	}

	@Override
	public int getComputedBattlePhysicalAttack() {
		return computedBattlePhysicalAttack();
	}

	@Override
	public int getComputedBattlePhysicalDefense() {
		return computedBattlePhysicalDefense();
	}

	@Override
	public int getComputedBattleSpeed() {
		return computedBattleSpeed();
	}

	@Override
	public void setHpAbsolute(final int absoluteHp) {
		battleStatus.setHp(absoluteHp * Constants.MAX_RELATIVE_HP / getComputedBattleMaxHp());
	}

	@Override
	public void setMpAbsolute(final int absoluteMp) {
		battleStatus.setMp(absoluteMp * Constants.MAX_RELATIVE_MP / getComputedBattleMaxMp());
	}

	private int computed(final int base, final int stage, final int max, @Nullable final IntToIntFunction condition) {
		final int pre = base * stageMultiplierNumerator[stage] / stageMultiplierDenominator[stage];
		final int post = condition == null ? pre : condition.apply(pre);
		return MathUtil.clamp(post, 0, max);
	}

	private int computedAccEv(final int base, final int stage, final int max,
			@Nullable final IntToIntFunction condition) {
		final int pre = base * stageMultiplierAccEvNumerator[stage] / stageMultiplierAccEvDenominator[stage];
		final int post = condition == null ? pre : condition.apply(pre);
		return MathUtil.clamp(post, 0, max);
	}

	private int computedBattleAccuracy() {
		final EStatusCondition condition = battleStatus.getStatusConditions();
		return computedAccEv(super.getComputedAccuracy(), battleStatus.getStageAccuracy(), Constants.MAX_ACCURACY,
				condition != null ? condition::adjustAccuracy : null);
	}

	private int computedBattleEvasion() {
		final EStatusCondition condition = battleStatus.getStatusConditions();
		return computedAccEv(super.getComputedEvasion(), battleStatus.getStageEvasion(), Constants.MAX_EVASION,
				condition != null ? condition::adjustEvasion : null);
	}

	private int computedBattleMagicalAttack() {
		final EStatusCondition condition = battleStatus.getStatusConditions();
		return computed(super.getComputedMagicalAttack(), battleStatus.getStageMagicalAttack(),
				Constants.MAX_MAGICAL_ATTACK, condition != null ? condition::adjustMagicalAttack : null);
	}

	private int computedBattleMagicalDefense() {
		final EStatusCondition condition = battleStatus.getStatusConditions();
		return computed(super.getComputedMagicalDefense(), battleStatus.getStageMagicalDefense(),
				Constants.MAX_MAGICAL_DEFENSE, condition != null ? condition::adjustMagicalDefense : null);
	}

	private int computedBattleMaxHp() {
		return super.getComputedMaxHp();
	}

	private int computedBattleMaxMp() {
		return super.getComputedMaxMp();
	}

	private int computedBattlePhysicalAttack() {
		final EStatusCondition condition = battleStatus.getStatusConditions();
		return computed(super.getComputedPhysicalAttack(), battleStatus.getStagePhysicalAttack(),
				Constants.MAX_PHYSICAL_ATTACK, condition != null ? condition::adjustPhysicalAttack : null);
	}

	private int computedBattlePhysicalDefense() {
		final EStatusCondition condition = battleStatus.getStatusConditions();
		return computed(super.getComputedPhysicalDefense(), battleStatus.getStagePhysicalDefense(),
				Constants.MAX_PHYSICAL_DEFENSE, condition != null ? condition::adjustPhysicalDefense : null);
	}

	private int computedBattleSpeed() {
		final EStatusCondition condition = battleStatus.getStatusConditions();
		return computed(super.getComputedSpeed(), battleStatus.getStageSpeed(), Constants.MAX_SPEED,
				condition != null ? condition::adjustSpeed : null);
	}

	@Override
	public void modifyHp(final int amountAbsolute) {
		final int currentHp = getComputedBattleHpAbsolute();
		final int newHp = currentHp + amountAbsolute;
		setHpAbsolute(newHp);
	}

	@Override
	public void modifyMp(final int amountAbsolute) {
		final int currentMp = getComputedBattleMpAbsolute();
		final int newMp = currentMp + amountAbsolute;
		setMpAbsolute(newMp);
	}
}