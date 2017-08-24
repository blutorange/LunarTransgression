package com.github.blutorange.translune.logic;

import java.util.Random;

import org.apache.commons.lang3.math.Fraction;
import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.translune.ic.ComponentFactory;

public enum EStatusCondition {
	POISON(f(1, 1), f(1, 1), f(1, 1), f(1, 1), f(1, 1), f(1,1), f(1,1)) {
		@Override
		public boolean canMove() {
			return true;
		}

		@Override
		public boolean disappears(final int turns) {
			return false;
		}

		@Override
		public Fraction getTurnEndDamage(final int turn) {
			return f(1, 16);
		}

		@Override
		public @NonNull String getTurnEndDamageMessage() {
			return "%s is hurt by poison and took %d damage.";
		}

		@Override
		public @NonNull String getDisappearsMessage() {
			return "%s was healed of its poison!";
		}

		@Override
		public @NonNull String getCannotMoveMessage() {
			return "%s is poisoned and cannot move";
		}
	},
	BAD_POISON(f(1, 1), f(1, 1), f(1, 1), f(1, 1), f(1, 1), f(1,1), f(1,1)) {
		@Override
		public boolean canMove() {
			return true;
		}

		@Override
		public boolean disappears(final int turns) {
			return false;
		}

		@Override
		public Fraction getTurnEndDamage(final int turn) {
			return f(1 + turn, 16);
		}

		@Override
		public @NonNull String getTurnEndDamageMessage() {
			return "%s is hurt badly by poison and took %d damage.";
		}

		@Override
		public @NonNull String getDisappearsMessage() {
			return "%s was healed of its bad poison!";
		}

		@Override
		public @NonNull String getCannotMoveMessage() {
			return "%s is poisoned badly and cannot move";
		}
	},
	SLEEP(f(1, 1), f(1, 1), f(1, 1), f(1, 1), f(1, 1), f(1,1), f(1,1)) {
		@Override
		public boolean canMove() {
			return false;
		}

		// Lasts 1-3 turns.
		@Override
		public boolean disappears(final int turns) {
			switch (turns) {
			case 0:
				return false;
			case 1:
				return random.nextInt(100) < 33;
			case 2:
				return random.nextInt(100) < 50;
			default:
				return true;
			}
		}

		@Override
		public Fraction getTurnEndDamage(final int turn) {
			return f(0, 1);
		}

		@Override
		public @NonNull String getTurnEndDamageMessage() {
			return "%s took %d damage while dreaming.";
		}

		@Override
		public @NonNull String getDisappearsMessage() {
			return "%s woke up!";
		}

		@Override
		public @NonNull String getCannotMoveMessage() {
			return "%s is fast asleep.";
		}
	},
	PARALYSIS(f(1, 1), f(1, 1), f(1, 1), f(1, 1), f(1, 2), f(1,1), f(1,1)) {
		@Override
		public boolean canMove() {
			return random.nextInt(100) >= 25;
		}

		@Override
		public boolean disappears(final int turns) {
			return false;
		}

		@Override
		public Fraction getTurnEndDamage(final int turn) {
			return f(0, 1);
		}

		@Override
		public @NonNull String getTurnEndDamageMessage() {
			return "%s is paralysed and took %d damage.";
		}

		@Override
		public @NonNull String getDisappearsMessage() {
			return "%s is not paralyzed anymore!";
		}

		@Override
		public @NonNull String getCannotMoveMessage() {
			return "%s is paralyzed and cannot move.";
		}
	},
	FREEZE(f(1, 1), f(1, 1), f(1, 1), f(1, 1), f(1, 1), f(1,1), f(1,1)) {
		@Override
		public boolean canMove() {
			return false;
		}

		@Override
		public boolean disappears(final int turns) {
			return random.nextInt(100) < 20;
		}

		@Override
		public Fraction getTurnEndDamage(final int turn) {
			return f(0, 1);
		}

		@Override
		public @NonNull String getTurnEndDamageMessage() {
			return "%s took %d damage from its freeze.";
		}

		@Override
		public @NonNull String getDisappearsMessage() {
			return "%s thawed out!";
		}

		@Override
		public @NonNull String getCannotMoveMessage() {
			return "%s is frozen solid.";
		}
	},
	BURN(f(1, 2), f(1, 1), f(1, 1), f(1, 1), f(1, 1), f(1,1), f(1,1)) {
		@Override
		public boolean canMove() {
			return true;
		}

		@Override
		public boolean disappears(final int turns) {
			return false;
		}

		@Override
		public @NonNull Fraction getTurnEndDamage(final int turn) {
			return f(1, 16);
		}

		@Override
		public String getTurnEndDamageMessage() {
			return "%s took %d damage from its burn.";
		}

		@Override
		public @NonNull String getDisappearsMessage() {
			return "%s was healed of its burn!";
		}

		@Override
		public @NonNull String getCannotMoveMessage() {
			return "%s's burn prevents it from moving.";
		}
	},
	// TODO [MID] implement hurt itself
	CONFUSE(f(1, 1), f(1, 1), f(1, 1), f(1, 1), f(1, 1), f(1,1), f(1,1)) {
		@Override
		public boolean canMove() {
			return random.nextInt(100) >= 33;
		}

		// Lasts 1-4 turns
		@Override
		public boolean disappears(final int turns) {
			switch (turns) {
			case 0:
				return false;
			case 1:
				return random.nextInt(100) < 25;
			case 2:
				return random.nextInt(100) < 33;
			case 3:
				return random.nextInt(100) < 50;
			default:
				return true;
			}
		}

		@Override
		public Fraction getTurnEndDamage(final int turn) {
			return f(0, 1);
		}

		@Override
		public @NonNull String getTurnEndDamageMessage() {
			return "%s took %d damage from its confusion.";
		}

		@Override
		public @NonNull String getDisappearsMessage() {
			return "%s snapped out of its confusion!";
		}

		@Override
		public @NonNull String getCannotMoveMessage() {
			return "%s is confused and cannot move.";
		}
	},;

	protected Random random;
	private Fraction physicalAttack;
	private Fraction physicalDefense;
	private Fraction magicalAttack;
	private Fraction magicalDefense;
	private Fraction speed;
	private Fraction evasion;
	private Fraction accuracy;

	protected static Fraction f(final int numerator, final int denominator) {
		return Fraction.getFraction(numerator, denominator);
	}

	private EStatusCondition(final Fraction physicalAttack, final Fraction physicalDefense,
			final Fraction magicalAttack, final Fraction magicalDefense, final Fraction speed, final Fraction accuracy,
			final Fraction evasion) {
		random = ComponentFactory.getLogicComponent().randomBasic();
		this.physicalAttack = physicalAttack;
		this.physicalDefense = physicalDefense;
		this.magicalAttack = magicalAttack;
		this.magicalDefense = magicalDefense;
		this.speed = speed;
		this.accuracy = accuracy;
		this.evasion = evasion;
	}

	public abstract boolean canMove();

	public abstract boolean disappears(int turns);

	public abstract Fraction getTurnEndDamage(int turn);

	public int adjustPhysicalAttack(final int value) {
		return value * physicalAttack.getNumerator() / physicalAttack.getDenominator();
	}

	public int adjustPhysicalDefense(final int value) {
		return value * physicalDefense.getNumerator() / physicalDefense.getDenominator();
	}

	public int adjustMagicalAttack(final int value) {
		return value * magicalAttack.getNumerator() / magicalAttack.getDenominator();
	}

	public int adjustMagicalDefense(final int value) {
		return value * magicalDefense.getNumerator() / magicalDefense.getDenominator();
	}

	public int adjustSpeed(final int value) {
		return value * speed.getNumerator() / speed.getDenominator();
	}

	public int adjustAccuracy(final int value) {
		return value * accuracy.getNumerator() / accuracy.getDenominator();
	}

	public int adjustEvasion(final int value) {
		return value * evasion.getNumerator() / evasion.getDenominator();
	}

	public abstract String getTurnEndDamageMessage();

	public abstract String getDisappearsMessage();

	public abstract String getCannotMoveMessage();
}