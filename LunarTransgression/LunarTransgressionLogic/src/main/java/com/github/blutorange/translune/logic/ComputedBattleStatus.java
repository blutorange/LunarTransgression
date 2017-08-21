package com.github.blutorange.translune.logic;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.util.Constants;
import com.github.blutorange.translune.util.IntIntFunction;
import com.github.blutorange.translune.util.MathUtil;

class ComputedBattleStatus extends ComputedStatus implements IComputedBattleStatus {
	private final static int[] stageMultiplierAccEvDenominator = new int[]{
			9,8,7,6,5,4,3,3,3,3,3,3,3
	};

	private final static int[] stageMultiplierAccEvNumerator = new int[]{
			3,3,3,3,3,3,3,4,5,6,7,8,9
	};

	private final static int[] stageMultiplierDenominator = new int[]{
			8,7,6,5,4,3,2,2,2,2,2,2,2
	};

	private final static int[] stageMultiplierNumerator = new int[]{
			2,2,2,2,2,2,2,3,4,5,6,7,8
	};

	private int _hp;

	private int _magicalAttack;

	private int _magicalDefense;

	private int _mp;

	private int _physicalAttack;

	private int _physicalDefense;

	private int _speed;

	private final BattleStatus battleStatus;

	private int _evasion;

	private int _accuracy;

	public ComputedBattleStatus(final CharacterState characterState, final BattleStatus battleStatus) {
		super(characterState);
		this.battleStatus = battleStatus;
		_update();
	}

	@Override
	public int getComputedBattleHp() {
		return _hp;
	}

	@Override
	public int getComputedBattleMagicalAttack() {
		return _magicalAttack;
	}

	@Override
	public int getComputedBattleMagicalDefense() {
		return _magicalDefense;
	}

	@Override
	public int getComputedBattleMp() {
		return _mp;
	}

	@Override
	public int getComputedBattlePhysicalAttack() {
		return _physicalAttack;
	}

	@Override
	public int getComputedBattlePhysicalDefense() {
		return _physicalDefense;
	}

	@Override
	public int getComputedBattleSpeed() {
		return _speed;
	}

	@Override
	public void update() {
		super.update();
		_update();
	}

	private void _update() {
		_hp = computedBattleHp();
		_mp = computedBattleMp();
		_physicalAttack = computedBattlePhysicalAttack();
		_physicalDefense = computedBattlePhysicalDefense();
		_magicalAttack = computedBattleMagicalAttack();
		_magicalDefense = computedBattleMagicalDefense();
		_speed = computedBattleSpeed();
		_accuracy = computedBattleAccuracy();
		_evasion = computedBattleEvasion();
	}

	private int computed(final int base, final int stage, final int max, @Nullable final IntIntFunction condition) {
		final int pre = base * stageMultiplierNumerator[stage] / stageMultiplierDenominator[stage];
		final int post = condition == null ? pre : condition.apply(pre);
		return MathUtil.clamp(post, 0, max);
	}

	private int computedAccEv(final int base, final int stage, final int max, @Nullable final IntIntFunction condition) {
		final int pre = base * stageMultiplierAccEvNumerator[stage] / stageMultiplierAccEvDenominator[stage];
		final int post = condition == null ? pre : condition.apply(pre);
		return MathUtil.clamp(post, 0, max);
	}

	private int computedBattleHp() {
		return super.getComputedHp();
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

	private int computedBattleMp() {
		return super.getComputedMp();
	}

	private int computedBattlePhysicalAttack() {
		final EStatusCondition condition = battleStatus.getStatusConditions();
		return computed(super.getComputedPhysicalAttack(), battleStatus.getStagePhysicalAttack(),
				Constants.MAX_PHYSICAL_ATTACK, condition != null ? condition::adjustPhysicalAttack : null);
	}

	private int computedBattleAccuracy() {
		final EStatusCondition condition = battleStatus.getStatusConditions();
		return computed(super.getComputedAccuracy(), battleStatus.getStageAccuracy(),
				Constants.MAX_ACCURACY, condition != null ? condition::adjustAccuracy : null);
	}

	private int computedBattleEvasion() {
		final EStatusCondition condition = battleStatus.getStatusConditions();
		return computed(super.getComputedEvasion(), battleStatus.getStageEvasion(),
				Constants.MAX_EVASION, condition != null ? condition::adjustEvasion : null);
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
	public int getComputedBattleAccuracy() {
		return _accuracy;
	}

	@Override
	public int getComputedBattleEvasion() {
		return _evasion;
	}
}