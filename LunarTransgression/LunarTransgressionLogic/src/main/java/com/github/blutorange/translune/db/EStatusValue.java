package com.github.blutorange.translune.db;

import com.github.blutorange.translune.logic.IBattleStatus;

public enum EStatusValue {
	STATUS_CONDITION(false) {
		@Override
		public void changeStage(final IBattleStatus bs, final int amount) {
			// Cannot change
		}
	},
	HP(false) {
		@Override
		public void changeStage(final IBattleStatus bs, final int amount) {
			// Cannot change
		}
	},
	MP(false) {
		@Override
		public void changeStage(final IBattleStatus bs, final int amount) {
			// Cannot change
		}
	},
	PHYSICAL_ATTACK(true) {
		@Override
		public void changeStage(final IBattleStatus bs, final int amount) {
			bs.changeStagePhysicalAttack(amount);
		}
	},
	PHYSICAL_DEFENSE(true) {
		@Override
		public void changeStage(final IBattleStatus bs, final int amount) {
			bs.changeStagePhysicalDefense(amount);
		}
	},
	MAGICAL_ATTACK(true) {
		@Override
		public void changeStage(final IBattleStatus bs, final int amount) {
			bs.changeStageMagicalAttack(amount);
		}
	},
	MAGICAL_DEFENSE(true) {
		@Override
		public void changeStage(final IBattleStatus bs, final int amount) {
			bs.changeStageMagicalDefense(amount);
		}
	},
	ACCURACY(true) {
		@Override
		public void changeStage(final IBattleStatus bs, final int amount) {
			bs.changeStageAccuracy(amount);
		}
	},
	EVASION(true) {
		@Override
		public void changeStage(final IBattleStatus bs, final int amount) {
			bs.changeStageEvasion(amount);
		}
	},
	SPEED(true) {
		@Override
		public void changeStage(final IBattleStatus bs, final int amount) {
			bs.changeStageSpeed(amount);
		}
	};
	private boolean canRaiseStage;

	private EStatusValue(final boolean canRaiseStage) {
		this.canRaiseStage = canRaiseStage;
	}

	public boolean canRaiseStage() {
		return canRaiseStage;
	}

	public abstract void changeStage(final IBattleStatus bs, final int amount);
}