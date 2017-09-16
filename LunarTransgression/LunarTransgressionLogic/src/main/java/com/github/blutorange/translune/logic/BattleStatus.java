package com.github.blutorange.translune.logic;

import org.apache.commons.math3.fraction.BigFraction;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.common.MathUtil;
import com.github.blutorange.translune.util.Constants;

public class BattleStatus implements IBattleStatus {
	private int hp;
	private int mp;
	private int stageAccuracy;
	private int stageEvasion;
	private int stageMagicalAttack;
	private int stageMagicalDefense;
	private int stagePhysicalAttack;
	private int stagePhysicalDefense;
	private int stageSpeed;
	@Nullable
	private EStatusCondition statusConditions = null;

	public BattleStatus() {
		hp = Constants.MAX_RELATIVE_HP;
		mp = Constants.MAX_RELATIVE_MP;
	}

	@Override
	public void changeStageAccuracy(final int amount) {
		stageAccuracy += amount;
	}

	@Override
	public void changeStageEvasion(final int amount) {
		stageEvasion += amount;
	}

	@Override
	public void changeStageMagicalAttack(final int amount) {
		stageMagicalAttack += amount;
	}

	@Override
	public void changeStageMagicalDefense(final int amount) {
		stageMagicalDefense += amount;
	}

	@Override
	public void changeStagePhysicalAttack(final int amount) {
		stagePhysicalAttack += amount;
	}

	@Override
	public void changeStagePhysicalDefense(final int amount) {
		stagePhysicalDefense += amount;
	}

	@Override
	public void changeStageSpeed(final int amount) {
		stageSpeed += amount;
	}

	@Override
	public int getHp() {
		return hp;
	}

	@Override
	public int getMp() {
		return mp;
	}

	@Override
	public int getStageAccuracy() {
		return clampStage(stageAccuracy);
	}

	@Override
	public int getStageEvasion() {
		return clampStage(stageEvasion);
	}

	@Override
	public int getStageMagicalAttack() {
		return clampStage(stageMagicalAttack);
	}

	@Override
	public int getStageMagicalDefense() {
		return clampStage(stageMagicalDefense);
	}

	@Override
	public int getStagePhysicalAttack() {
		return clampStage(stagePhysicalAttack);
	}

	@Override
	public int getStagePhysicalDefense() {
		return clampStage(stagePhysicalDefense);
	}

	@Override
	public int getStageSpeed() {
		return clampStage(stageSpeed);
	}

	@Nullable
	@Override
	public EStatusCondition getStatusCondition() {
		return statusConditions;
	}

	@Override
	public void setHp(final int hp) {
		this.hp = MathUtil.clamp(hp, 0, Constants.MAX_RELATIVE_HP);
	}

	@Override
	public void setMp(final int mp) {
		this.mp = MathUtil.clamp(mp, 0, Constants.MAX_RELATIVE_MP);
	}

	@Override
	public void setStageAccuracy(final int stageAccuracy) {
		this.stageAccuracy = stageAccuracy;
	}

	@Override
	public void setStageEvasion(final int stageEvasion) {
		this.stageEvasion = stageEvasion;
	}

	@Override
	public void setStageMagicalAttack(final int stageMagicalAttack) {
		this.stageMagicalAttack = stageMagicalAttack;
	}

	@Override
	public void setStageMagicalDefense(final int stageMagicalDefense) {
		this.stageMagicalDefense = stageMagicalDefense;
	}

	@Override
	public void setStagePhysicalAttack(final int stagePhysicalAttack) {
		this.stagePhysicalAttack = stagePhysicalAttack;
	}

	@Override
	public void setStagePhysicalDefense(final int stagePhysicalDefense) {
		this.stagePhysicalDefense = stagePhysicalDefense;
	}

	@Override
	public void setStageSpeed(final int stageSpeed) {
		this.stageSpeed = stageSpeed;
	}

	@Override
	public void setStatusCondition(final EStatusCondition statusConditions) {
		this.statusConditions = statusConditions;
	}

	private int clampStage(final int stage) {
		return MathUtil.clamp(stage, Constants.MIN_STAGE, Constants.MAX_STAGE);
	}

	/* (non-Javadoc)
	 * @see com.github.blutorange.translune.logic.IBattleStatus#getHpFraction()
	 */
	@Override
	public BigFraction getHpFraction() {
		return BigFraction.getReducedFraction(hp, Constants.MAX_RELATIVE_HP);
	}

	/* (non-Javadoc)
	 * @see com.github.blutorange.translune.logic.IBattleStatus#getMpFraction()
	 */
	@Override
	public BigFraction getMpFraction() {
		return BigFraction.getReducedFraction(mp, Constants.MAX_RELATIVE_MP);
	}

	@Override
	public IBattleStatus copy() {
		return new BattleStatusSnapshot();
	}

	@SuppressWarnings("hiding")
	private class BattleStatusSnapshot extends BattleStatus {
		private final int hp;
		private final int mp;
		private final int stageAccuracy;
		private final int stageEvasion;
		private final int stageMagicalAttack;
		private final int stageMagicalDefense;
		private final int stagePhysicalAttack;
		private final int stagePhysicalDefense;
		private final int stageSpeed;
		private @Nullable
		final EStatusCondition statusCondition;

		public BattleStatusSnapshot() {
			this.hp = BattleStatus.this.getHp();
			this.mp = BattleStatus.this.getMp();
			this.stageAccuracy = BattleStatus.this.getStageAccuracy();
			this.stageEvasion = BattleStatus.this.getStageEvasion();
			this.stageMagicalAttack = BattleStatus.this.getStageMagicalAttack();
			this.stageMagicalDefense = BattleStatus.this.getStageMagicalDefense();
			this.stagePhysicalAttack = BattleStatus.this.getStagePhysicalAttack();
			this.stagePhysicalDefense = BattleStatus.this.getStagePhysicalDefense();
			this.stageSpeed = BattleStatus.this.getStageSpeed();
			this.statusCondition = BattleStatus.this.getStatusCondition();
		}

		@Override
		public int getHp() {
			return hp;
		}

		@Override
		public int getMp() {
			return mp;
		}

		@Override
		public int getStageAccuracy() {
			return stageAccuracy;
		}

		@Override
		public int getStageEvasion() {
			return stageEvasion;
		}

		@Override
		public int getStageMagicalAttack() {
			return stageMagicalAttack;
		}

		@Override
		public int getStageMagicalDefense() {
			return stageMagicalDefense;
		}

		@Override
		public int getStagePhysicalAttack() {
			return stagePhysicalAttack;
		}

		@Override
		public int getStagePhysicalDefense() {
			return stagePhysicalDefense;
		}

		@Override
		public int getStageSpeed() {
			return stageSpeed;
		}

		@Override
		public @Nullable EStatusCondition getStatusCondition() {
			return statusCondition;
		}

		@Override
		public BigFraction getHpFraction() {
			return BigFraction.getReducedFraction(hp, Constants.MAX_RELATIVE_HP);
		}

		@Override
		public BigFraction getMpFraction() {
			return BigFraction.getReducedFraction(mp, Constants.MAX_RELATIVE_MP);
		}

		@Override
		public IBattleStatus copy() {
			return this;
		}
	}
}