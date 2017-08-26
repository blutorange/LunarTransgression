package com.github.blutorange.translune.logic;

import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.lang3.math.Fraction;

import com.github.blutorange.translune.util.Constants;

public enum EElement {
	BUG(true),
	DARK(false),
	DRAGON(false),
	ELECTRIC(false),
	FAIRY(false),
	FIGHTING(true),
	FIRE(false),
	FLYING(true),
	GHOST(true),
	GRASS(false),
	GROUND(true),
	ICE(false),
	NORMAL(true),
	POISON(true),
	PSYCHIC(false),
	ROCK(true),
	STEEL(true),
	WATER(false);

	private static enum TypeEffectivenessMap {
		INSTANCE;

		private static Map<EElement, Fraction> EMPTY_MAP = new EnumMap<>(EElement.class);

		public final Map<EElement, Map<EElement, Fraction>> map;

		private TypeEffectivenessMap() {
			map = new EnumMap<>(EElement.class);
			for (final EElement element : EElement.values()) {
				final Map<EElement, Fraction> submap = new EnumMap<>(EElement.class);
				map.put(element, submap);
			}
			get(NORMAL).put(ROCK, Fraction.ONE_HALF);
			get(NORMAL).put(GHOST, Fraction.ZERO);
			get(NORMAL).put(STEEL, Fraction.ONE_HALF);

			get(FIGHTING).put(NORMAL, Constants.FRACTION_TWO);
			get(FIGHTING).put(FLYING, Fraction.ONE_HALF);
			get(FIGHTING).put(POISON, Fraction.ONE_HALF);
			get(FIGHTING).put(ROCK, Constants.FRACTION_TWO);
			get(FIGHTING).put(BUG, Fraction.ONE_HALF);
			get(FIGHTING).put(GHOST, Fraction.ZERO);
			get(FIGHTING).put(STEEL, Constants.FRACTION_TWO);
			get(FIGHTING).put(PSYCHIC, Fraction.ONE_HALF);
			get(FIGHTING).put(ICE, Constants.FRACTION_TWO);
			get(FIGHTING).put(DARK, Constants.FRACTION_TWO);
			get(FIGHTING).put(FAIRY, Fraction.ONE_HALF);

			get(FLYING).put(FIGHTING, Constants.FRACTION_TWO);
			get(FLYING).put(ROCK, Fraction.ONE_HALF);
			get(FLYING).put(BUG, Constants.FRACTION_TWO);
			get(FLYING).put(STEEL, Fraction.ONE_HALF);
			get(FLYING).put(GRASS, Constants.FRACTION_TWO);
			get(FLYING).put(ELECTRIC, Fraction.ONE_HALF);

			get(POISON).put(POISON, Fraction.ONE_HALF);
			get(POISON).put(GROUND, Fraction.ONE_HALF);
			get(POISON).put(ROCK, Fraction.ONE_HALF);
			get(POISON).put(GHOST, Fraction.ONE_HALF);
			get(POISON).put(STEEL, Fraction.ZERO);
			get(POISON).put(GRASS, Constants.FRACTION_TWO);
			get(POISON).put(DARK, Constants.FRACTION_TWO);

			get(GROUND).put(FLYING, Fraction.ZERO);
			get(GROUND).put(ROCK, Constants.FRACTION_TWO);
			get(GROUND).put(POISON, Constants.FRACTION_TWO);
			get(GROUND).put(BUG, Fraction.ONE_HALF);
			get(GROUND).put(STEEL, Constants.FRACTION_TWO);
			get(GROUND).put(FIRE, Constants.FRACTION_TWO);
			get(GROUND).put(GRASS, Fraction.ONE_HALF);
			get(GROUND).put(ELECTRIC, Constants.FRACTION_TWO);

			get(ROCK).put(FIGHTING, Fraction.ONE_HALF);
			get(ROCK).put(FLYING, Constants.FRACTION_TWO);
			get(ROCK).put(GROUND, Fraction.ONE_HALF);
			get(ROCK).put(BUG, Constants.FRACTION_TWO);
			get(ROCK).put(STEEL, Fraction.ONE_HALF);
			get(ROCK).put(FIRE, Constants.FRACTION_TWO);
			get(ROCK).put(ICE, Constants.FRACTION_TWO);

			get(BUG).put(FIGHTING, Fraction.ONE_HALF);
			get(BUG).put(FLYING, Fraction.ONE_HALF);
			get(BUG).put(POISON, Fraction.ONE_HALF);
			get(BUG).put(GHOST, Fraction.ONE_HALF);
			get(BUG).put(STEEL, Fraction.ONE_HALF);
			get(BUG).put(FIRE, Fraction.ONE_HALF);
			get(BUG).put(GRASS, Constants.FRACTION_TWO);
			get(BUG).put(PSYCHIC, Constants.FRACTION_TWO);
			get(BUG).put(DARK, Constants.FRACTION_TWO);
			get(BUG).put(FAIRY, Fraction.ONE_HALF);

			get(GHOST).put(NORMAL, Fraction.ZERO);
			get(GHOST).put(GHOST, Constants.FRACTION_TWO);
			get(GHOST).put(PSYCHIC, Constants.FRACTION_TWO);
			get(GHOST).put(DARK, Fraction.ONE_HALF);

			get(STEEL).put(ROCK, Constants.FRACTION_TWO);
			get(STEEL).put(STEEL, Fraction.ONE_HALF);
			get(STEEL).put(FIRE, Fraction.ONE_HALF);
			get(STEEL).put(WATER, Fraction.ONE_HALF);
			get(STEEL).put(ELECTRIC, Fraction.ONE_HALF);
			get(STEEL).put(ICE, Constants.FRACTION_TWO);
			get(STEEL).put(FAIRY, Constants.FRACTION_TWO);

			get(FIRE).put(ROCK, Fraction.ONE_HALF);
			get(FIRE).put(BUG, Constants.FRACTION_TWO);
			get(FIRE).put(STEEL, Constants.FRACTION_TWO);
			get(FIRE).put(FIRE, Fraction.ONE_HALF);
			get(FIRE).put(WATER, Fraction.ONE_HALF);
			get(FIRE).put(GRASS, Constants.FRACTION_TWO);
			get(FIRE).put(ICE, Constants.FRACTION_TWO);
			get(FIRE).put(DRAGON, Fraction.ONE_HALF);

			get(WATER).put(GROUND, Constants.FRACTION_TWO);
			get(WATER).put(ROCK, Constants.FRACTION_TWO);
			get(WATER).put(FIRE, Constants.FRACTION_TWO);
			get(WATER).put(WATER, Fraction.ONE_HALF);
			get(WATER).put(GRASS, Fraction.ONE_HALF);
			get(WATER).put(DRAGON, Fraction.ONE_HALF);

			get(GRASS).put(FLYING, Fraction.ONE_HALF);
			get(GRASS).put(POISON, Fraction.ONE_HALF);
			get(GRASS).put(GROUND, Constants.FRACTION_TWO);
			get(GRASS).put(ROCK, Constants.FRACTION_TWO);
			get(GRASS).put(BUG, Fraction.ONE_HALF);
			get(GRASS).put(STEEL, Fraction.ONE_HALF);
			get(GRASS).put(FIRE, Fraction.ONE_HALF);
			get(GRASS).put(WATER, Constants.FRACTION_TWO);
			get(GRASS).put(GRASS, Fraction.ONE_HALF);
			get(GRASS).put(DRAGON, Fraction.ONE_HALF);

			get(ELECTRIC).put(FLYING, Constants.FRACTION_TWO);
			get(ELECTRIC).put(GROUND, Fraction.ZERO);
			get(ELECTRIC).put(WATER, Constants.FRACTION_TWO);
			get(ELECTRIC).put(GRASS, Fraction.ONE_HALF);
			get(ELECTRIC).put(ELECTRIC, Fraction.ONE_HALF);
			get(ELECTRIC).put(DRAGON, Fraction.ONE_HALF);

			get(PSYCHIC).put(FIGHTING, Constants.FRACTION_TWO);
			get(PSYCHIC).put(POISON, Constants.FRACTION_TWO);
			get(PSYCHIC).put(STEEL, Fraction.ONE_HALF);
			get(PSYCHIC).put(PSYCHIC, Fraction.ONE_HALF);
			get(PSYCHIC).put(DARK, Fraction.ZERO);

			get(ICE).put(FLYING, Constants.FRACTION_TWO);
			get(ICE).put(GROUND, Constants.FRACTION_TWO);
			get(ICE).put(STEEL, Fraction.ONE_HALF);
			get(ICE).put(FIRE, Fraction.ONE_HALF);
			get(ICE).put(WATER, Fraction.ONE_HALF);
			get(ICE).put(GRASS, Constants.FRACTION_TWO);
			get(ICE).put(PSYCHIC, Fraction.ONE_HALF);
			get(ICE).put(DRAGON, Constants.FRACTION_TWO);

			get(DRAGON).put(STEEL, Fraction.ONE_HALF);
			get(DRAGON).put(DRAGON, Constants.FRACTION_TWO);
			get(DRAGON).put(FAIRY, Fraction.ZERO);

			get(DARK).put(FIGHTING, Fraction.ONE_HALF);
			get(DARK).put(GHOST, Constants.FRACTION_TWO);
			get(DARK).put(ELECTRIC, Constants.FRACTION_TWO);
			get(DARK).put(DARK, Fraction.ONE_HALF);
			get(DARK).put(FAIRY, Fraction.ONE_HALF);

			get(FAIRY).put(FIGHTING, Constants.FRACTION_TWO);
			get(FAIRY).put(POISON, Fraction.ONE_HALF);
			get(FAIRY).put(STEEL, Fraction.ONE_HALF);
			get(FAIRY).put(FIRE, Fraction.ONE_HALF);
			get(FAIRY).put(DRAGON, Constants.FRACTION_TWO);
			get(FAIRY).put(DARK, Constants.FRACTION_TWO);
		}

		protected Map<EElement, Fraction> get(final EElement element) {
			return map.getOrDefault(element, EMPTY_MAP);
		}
	}

	private boolean isPhysical;

	private EElement(final boolean isPhysical) {
		this.isPhysical = isPhysical;
	}

	public boolean isPhysical() {
		return isPhysical;
	}

	public Fraction typeEffectivness(final EElement other) {
		return TypeEffectivenessMap.INSTANCE.get(this).getOrDefault(other, Fraction.ONE);
	}
}