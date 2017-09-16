package com.github.blutorange.translune.logic;

import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.math3.fraction.BigFraction;

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

		private static Map<EElement, BigFraction> EMPTY_MAP = new EnumMap<>(EElement.class);

		public final Map<EElement, Map<EElement, BigFraction>> map;

		private TypeEffectivenessMap() {
			map = new EnumMap<>(EElement.class);
			for (final EElement element : EElement.values()) {
				final Map<EElement, BigFraction> submap = new EnumMap<>(EElement.class);
				map.put(element, submap);
			}
			get(NORMAL).put(ROCK, BigFraction.ONE_HALF);
			get(NORMAL).put(GHOST, BigFraction.ZERO);
			get(NORMAL).put(STEEL, BigFraction.ONE_HALF);

			get(FIGHTING).put(NORMAL, Constants.FRACTION_TWO);
			get(FIGHTING).put(FLYING, BigFraction.ONE_HALF);
			get(FIGHTING).put(POISON, BigFraction.ONE_HALF);
			get(FIGHTING).put(ROCK, Constants.FRACTION_TWO);
			get(FIGHTING).put(BUG, BigFraction.ONE_HALF);
			get(FIGHTING).put(GHOST, BigFraction.ZERO);
			get(FIGHTING).put(STEEL, Constants.FRACTION_TWO);
			get(FIGHTING).put(PSYCHIC, BigFraction.ONE_HALF);
			get(FIGHTING).put(ICE, Constants.FRACTION_TWO);
			get(FIGHTING).put(DARK, Constants.FRACTION_TWO);
			get(FIGHTING).put(FAIRY, BigFraction.ONE_HALF);

			get(FLYING).put(FIGHTING, Constants.FRACTION_TWO);
			get(FLYING).put(ROCK, BigFraction.ONE_HALF);
			get(FLYING).put(BUG, Constants.FRACTION_TWO);
			get(FLYING).put(STEEL, BigFraction.ONE_HALF);
			get(FLYING).put(GRASS, Constants.FRACTION_TWO);
			get(FLYING).put(ELECTRIC, BigFraction.ONE_HALF);

			get(POISON).put(POISON, BigFraction.ONE_HALF);
			get(POISON).put(GROUND, BigFraction.ONE_HALF);
			get(POISON).put(ROCK, BigFraction.ONE_HALF);
			get(POISON).put(GHOST, BigFraction.ONE_HALF);
			get(POISON).put(STEEL, BigFraction.ZERO);
			get(POISON).put(GRASS, Constants.FRACTION_TWO);
			get(POISON).put(DARK, Constants.FRACTION_TWO);

			get(GROUND).put(FLYING, BigFraction.ZERO);
			get(GROUND).put(ROCK, Constants.FRACTION_TWO);
			get(GROUND).put(POISON, Constants.FRACTION_TWO);
			get(GROUND).put(BUG, BigFraction.ONE_HALF);
			get(GROUND).put(STEEL, Constants.FRACTION_TWO);
			get(GROUND).put(FIRE, Constants.FRACTION_TWO);
			get(GROUND).put(GRASS, BigFraction.ONE_HALF);
			get(GROUND).put(ELECTRIC, Constants.FRACTION_TWO);

			get(ROCK).put(FIGHTING, BigFraction.ONE_HALF);
			get(ROCK).put(FLYING, Constants.FRACTION_TWO);
			get(ROCK).put(GROUND, BigFraction.ONE_HALF);
			get(ROCK).put(BUG, Constants.FRACTION_TWO);
			get(ROCK).put(STEEL, BigFraction.ONE_HALF);
			get(ROCK).put(FIRE, Constants.FRACTION_TWO);
			get(ROCK).put(ICE, Constants.FRACTION_TWO);

			get(BUG).put(FIGHTING, BigFraction.ONE_HALF);
			get(BUG).put(FLYING, BigFraction.ONE_HALF);
			get(BUG).put(POISON, BigFraction.ONE_HALF);
			get(BUG).put(GHOST, BigFraction.ONE_HALF);
			get(BUG).put(STEEL, BigFraction.ONE_HALF);
			get(BUG).put(FIRE, BigFraction.ONE_HALF);
			get(BUG).put(GRASS, Constants.FRACTION_TWO);
			get(BUG).put(PSYCHIC, Constants.FRACTION_TWO);
			get(BUG).put(DARK, Constants.FRACTION_TWO);
			get(BUG).put(FAIRY, BigFraction.ONE_HALF);

			get(GHOST).put(NORMAL, BigFraction.ZERO);
			get(GHOST).put(GHOST, Constants.FRACTION_TWO);
			get(GHOST).put(PSYCHIC, Constants.FRACTION_TWO);
			get(GHOST).put(DARK, BigFraction.ONE_HALF);

			get(STEEL).put(ROCK, Constants.FRACTION_TWO);
			get(STEEL).put(STEEL, BigFraction.ONE_HALF);
			get(STEEL).put(FIRE, BigFraction.ONE_HALF);
			get(STEEL).put(WATER, BigFraction.ONE_HALF);
			get(STEEL).put(ELECTRIC, BigFraction.ONE_HALF);
			get(STEEL).put(ICE, Constants.FRACTION_TWO);
			get(STEEL).put(FAIRY, Constants.FRACTION_TWO);

			get(FIRE).put(ROCK, BigFraction.ONE_HALF);
			get(FIRE).put(BUG, Constants.FRACTION_TWO);
			get(FIRE).put(STEEL, Constants.FRACTION_TWO);
			get(FIRE).put(FIRE, BigFraction.ONE_HALF);
			get(FIRE).put(WATER, BigFraction.ONE_HALF);
			get(FIRE).put(GRASS, Constants.FRACTION_TWO);
			get(FIRE).put(ICE, Constants.FRACTION_TWO);
			get(FIRE).put(DRAGON, BigFraction.ONE_HALF);

			get(WATER).put(GROUND, Constants.FRACTION_TWO);
			get(WATER).put(ROCK, Constants.FRACTION_TWO);
			get(WATER).put(FIRE, Constants.FRACTION_TWO);
			get(WATER).put(WATER, BigFraction.ONE_HALF);
			get(WATER).put(GRASS, BigFraction.ONE_HALF);
			get(WATER).put(DRAGON, BigFraction.ONE_HALF);

			get(GRASS).put(FLYING, BigFraction.ONE_HALF);
			get(GRASS).put(POISON, BigFraction.ONE_HALF);
			get(GRASS).put(GROUND, Constants.FRACTION_TWO);
			get(GRASS).put(ROCK, Constants.FRACTION_TWO);
			get(GRASS).put(BUG, BigFraction.ONE_HALF);
			get(GRASS).put(STEEL, BigFraction.ONE_HALF);
			get(GRASS).put(FIRE, BigFraction.ONE_HALF);
			get(GRASS).put(WATER, Constants.FRACTION_TWO);
			get(GRASS).put(GRASS, BigFraction.ONE_HALF);
			get(GRASS).put(DRAGON, BigFraction.ONE_HALF);

			get(ELECTRIC).put(FLYING, Constants.FRACTION_TWO);
			get(ELECTRIC).put(GROUND, BigFraction.ZERO);
			get(ELECTRIC).put(WATER, Constants.FRACTION_TWO);
			get(ELECTRIC).put(GRASS, BigFraction.ONE_HALF);
			get(ELECTRIC).put(ELECTRIC, BigFraction.ONE_HALF);
			get(ELECTRIC).put(DRAGON, BigFraction.ONE_HALF);

			get(PSYCHIC).put(FIGHTING, Constants.FRACTION_TWO);
			get(PSYCHIC).put(POISON, Constants.FRACTION_TWO);
			get(PSYCHIC).put(STEEL, BigFraction.ONE_HALF);
			get(PSYCHIC).put(PSYCHIC, BigFraction.ONE_HALF);
			get(PSYCHIC).put(DARK, BigFraction.ZERO);

			get(ICE).put(FLYING, Constants.FRACTION_TWO);
			get(ICE).put(GROUND, Constants.FRACTION_TWO);
			get(ICE).put(STEEL, BigFraction.ONE_HALF);
			get(ICE).put(FIRE, BigFraction.ONE_HALF);
			get(ICE).put(WATER, BigFraction.ONE_HALF);
			get(ICE).put(GRASS, Constants.FRACTION_TWO);
			get(ICE).put(PSYCHIC, BigFraction.ONE_HALF);
			get(ICE).put(DRAGON, Constants.FRACTION_TWO);

			get(DRAGON).put(STEEL, BigFraction.ONE_HALF);
			get(DRAGON).put(DRAGON, Constants.FRACTION_TWO);
			get(DRAGON).put(FAIRY, BigFraction.ZERO);

			get(DARK).put(FIGHTING, BigFraction.ONE_HALF);
			get(DARK).put(GHOST, Constants.FRACTION_TWO);
			get(DARK).put(ELECTRIC, Constants.FRACTION_TWO);
			get(DARK).put(DARK, BigFraction.ONE_HALF);
			get(DARK).put(FAIRY, BigFraction.ONE_HALF);

			get(FAIRY).put(FIGHTING, Constants.FRACTION_TWO);
			get(FAIRY).put(POISON, BigFraction.ONE_HALF);
			get(FAIRY).put(STEEL, BigFraction.ONE_HALF);
			get(FAIRY).put(FIRE, BigFraction.ONE_HALF);
			get(FAIRY).put(DRAGON, Constants.FRACTION_TWO);
			get(FAIRY).put(DARK, Constants.FRACTION_TWO);
		}

		protected Map<EElement, BigFraction> get(final EElement element) {
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

	public BigFraction typeEffectivness(final EElement other) {
		return TypeEffectivenessMap.INSTANCE.get(this).getOrDefault(other, BigFraction.ONE);
	}
}