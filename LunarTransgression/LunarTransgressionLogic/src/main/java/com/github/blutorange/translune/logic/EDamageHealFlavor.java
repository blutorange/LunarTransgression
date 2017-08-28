package com.github.blutorange.translune.logic;

import org.eclipse.jdt.annotation.NonNull;

public enum EDamageHealFlavor {
	NONE(0) {
		@Override
		public boolean preconditionFulfilled(@NonNull final IComputedBattleStatus target) {
			return true;
		}
	},
	ABSORB(50) {
		@Override
		public boolean preconditionFulfilled(@NonNull final IComputedBattleStatus target) {
			return true;
		}
	},
	DRAIN_PUNCH(50) {
		@Override
		public boolean preconditionFulfilled(@NonNull final IComputedBattleStatus target) {
			return true;
		}
	},
	DRAINING_KISS(75) {
		@Override
		public boolean preconditionFulfilled(@NonNull final IComputedBattleStatus target) {
			return true;
		}
	},
	DREAM_EATER(50) {
		@Override
		public boolean preconditionFulfilled(@NonNull final IComputedBattleStatus target) {
			return EStatusCondition.SLEEP.equals(target.getBattleStatus().getStatusCondition());
		}
	},
	GIGA_DRAIN(50) {
		@Override
		public boolean preconditionFulfilled(@NonNull final IComputedBattleStatus target) {
			return true;
		}
	},
	HORN_LEECH(50) {
		@Override
		public boolean preconditionFulfilled(@NonNull final IComputedBattleStatus target) {
			return true;
		}
	},
	LEECH_LIFE(50) {
		@Override
		public boolean preconditionFulfilled(@NonNull final IComputedBattleStatus target) {
			return true;
		}
	},
	MEGA_DRAIN(50) {
		@Override
		public boolean preconditionFulfilled(@NonNull final IComputedBattleStatus target) {
			return true;
		}
	},
	OBLIVION_WING(75) {
		@Override
		public boolean preconditionFulfilled(@NonNull final IComputedBattleStatus target) {
			return true;
		}
	},
	PARABOLIC_CHARGE(50) {
		@Override
		public boolean preconditionFulfilled(@NonNull final IComputedBattleStatus target) {
			return true;
		}
	},
	;
	private final int relativeAmount;

	private EDamageHealFlavor(final int relativeAmount) {
		this.relativeAmount = relativeAmount;
	}

	public int getRelativeAmount() {
		return relativeAmount;
	}

	public abstract boolean preconditionFulfilled(final IComputedBattleStatus target);
}