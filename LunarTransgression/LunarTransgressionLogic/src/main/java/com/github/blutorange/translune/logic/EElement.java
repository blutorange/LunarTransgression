package com.github.blutorange.translune.logic;

import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.lang3.math.Fraction;

public enum EElement {
	PHYSICAL,
	NORMAL,
	FIRE,
	FIGHTING,
	WATER,
	FLYING,
	GRASS,
	POISON,
	ELECTRIC,
	GROUND,
	PSYCHIC,
	ROCK,
	ICE,
	BUG,
	DRAGON,
	GHOST,
	DARK,
	STEEL,
	FAIRY;

	protected static Fraction TWO = Fraction.getFraction(2, 1);

	private EElement() {
	}

	public Fraction typeEffectivness(final EElement other) {
		return TypeEffectivenessMap.INSTANCE.get(this).getOrDefault(other, Fraction.ONE);
	}

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

			get(FIGHTING).put(NORMAL, TWO);
			get(FIGHTING).put(FLYING, Fraction.ONE_HALF);
			get(FIGHTING).put(POISON, Fraction.ONE_HALF);
			get(FIGHTING).put(ROCK, TWO);
			get(FIGHTING).put(BUG, Fraction.ONE_HALF);
			get(FIGHTING).put(GHOST, Fraction.ZERO);
			get(FIGHTING).put(STEEL, TWO);
			get(FIGHTING).put(PSYCHIC, Fraction.ONE_HALF);
			get(FIGHTING).put(ICE, TWO);
			get(FIGHTING).put(DARK, TWO);
			get(FIGHTING).put(FAIRY, Fraction.ONE_HALF);

			get(FLYING).put(FIGHTING, TWO);
			get(FLYING).put(ROCK, Fraction.ONE_HALF);
			get(FLYING).put(BUG, TWO);
			get(FLYING).put(STEEL, Fraction.ONE_HALF);
			get(FLYING).put(GRASS, TWO);
			get(FLYING).put(ELECTRIC, Fraction.ONE_HALF);

			get(POISON).put(POISON, Fraction.ONE_HALF);
			get(POISON).put(GROUND, Fraction.ONE_HALF);
			get(POISON).put(ROCK, Fraction.ONE_HALF);
			get(POISON).put(GHOST, Fraction.ONE_HALF);
			get(POISON).put(STEEL, Fraction.ZERO);
			get(POISON).put(GRASS, TWO);
			get(POISON).put(DARK, TWO);

			get(GROUND).put(FLYING, Fraction.ZERO);
			get(GROUND).put(ROCK, TWO);
			get(GROUND).put(POISON, TWO);
			get(GROUND).put(BUG, Fraction.ONE_HALF);
			get(GROUND).put(STEEL, TWO);
			get(GROUND).put(FIRE, TWO);
			get(GROUND).put(GRASS, Fraction.ONE_HALF);
			get(GROUND).put(ELECTRIC, TWO);

			get(ROCK).put(FIGHTING, Fraction.ONE_HALF);
			get(ROCK).put(FLYING, TWO);
			get(ROCK).put(GROUND, Fraction.ONE_HALF);
			get(ROCK).put(BUG, TWO);
			get(ROCK).put(STEEL, Fraction.ONE_HALF);
			get(ROCK).put(FIRE, TWO);
			get(ROCK).put(ICE, TWO);

			get(BUG).put(FIGHTING, Fraction.ONE_HALF);
			get(BUG).put(FLYING, Fraction.ONE_HALF);
			get(BUG).put(POISON, Fraction.ONE_HALF);
			get(BUG).put(GHOST, Fraction.ONE_HALF);
			get(BUG).put(STEEL, Fraction.ONE_HALF);
			get(BUG).put(FIRE, Fraction.ONE_HALF);
			get(BUG).put(GRASS, TWO);
			get(BUG).put(PSYCHIC, TWO);
			get(BUG).put(DARK, TWO);
			get(BUG).put(FAIRY, Fraction.ONE_HALF);

			get(GHOST).put(NORMAL, Fraction.ZERO);
			get(GHOST).put(GHOST, TWO);
			get(GHOST).put(PSYCHIC, TWO);
			get(GHOST).put(DARK, Fraction.ONE_HALF);

			get(STEEL).put(ROCK, TWO);
			get(STEEL).put(STEEL, Fraction.ONE_HALF);
			get(STEEL).put(FIRE, Fraction.ONE_HALF);
			get(STEEL).put(WATER, Fraction.ONE_HALF);
			get(STEEL).put(ELECTRIC, Fraction.ONE_HALF);
			get(STEEL).put(ICE, TWO);
			get(STEEL).put(FAIRY, TWO);

			get(FIRE).put(ROCK, Fraction.ONE_HALF);
			get(FIRE).put(BUG, TWO);
			get(FIRE).put(STEEL, TWO);
			get(FIRE).put(FIRE, Fraction.ONE_HALF);
			get(FIRE).put(WATER, Fraction.ONE_HALF);
			get(FIRE).put(GRASS, TWO);
			get(FIRE).put(ICE, TWO);
			get(FIRE).put(DRAGON, Fraction.ONE_HALF);

			get(WATER).put(GROUND, TWO);
			get(WATER).put(ROCK, TWO);
			get(WATER).put(FIRE, TWO);
			get(WATER).put(WATER, Fraction.ONE_HALF);
			get(WATER).put(GRASS, Fraction.ONE_HALF);
			get(WATER).put(DRAGON, Fraction.ONE_HALF);

			get(GRASS).put(FLYING, Fraction.ONE_HALF);
			get(GRASS).put(POISON, Fraction.ONE_HALF);
			get(GRASS).put(GROUND, TWO);
			get(GRASS).put(ROCK, TWO);
			get(GRASS).put(BUG, Fraction.ONE_HALF);
			get(GRASS).put(STEEL, Fraction.ONE_HALF);
			get(GRASS).put(FIRE, Fraction.ONE_HALF);
			get(GRASS).put(WATER, TWO);
			get(GRASS).put(GRASS, Fraction.ONE_HALF);
			get(GRASS).put(DRAGON, Fraction.ONE_HALF);

			get(ELECTRIC).put(FLYING, TWO);
			get(ELECTRIC).put(GROUND, Fraction.ZERO);
			get(ELECTRIC).put(WATER, TWO);
			get(ELECTRIC).put(GRASS, Fraction.ONE_HALF);
			get(ELECTRIC).put(ELECTRIC, Fraction.ONE_HALF);
			get(ELECTRIC).put(DRAGON, Fraction.ONE_HALF);

			get(PSYCHIC).put(FIGHTING, TWO);
			get(PSYCHIC).put(POISON, TWO);
			get(PSYCHIC).put(STEEL, Fraction.ONE_HALF);
			get(PSYCHIC).put(PSYCHIC, Fraction.ONE_HALF);
			get(PSYCHIC).put(DARK, Fraction.ZERO);

			get(ICE).put(FLYING, TWO);
			get(ICE).put(GROUND, TWO);
			get(ICE).put(STEEL, Fraction.ONE_HALF);
			get(ICE).put(FIRE, Fraction.ONE_HALF);
			get(ICE).put(WATER, Fraction.ONE_HALF);
			get(ICE).put(GRASS, TWO);
			get(ICE).put(PSYCHIC, Fraction.ONE_HALF);
			get(ICE).put(DRAGON, TWO);

			get(DRAGON).put(STEEL, Fraction.ONE_HALF);
			get(DRAGON).put(DRAGON, TWO);
			get(DRAGON).put(FAIRY, Fraction.ZERO);

			get(DARK).put(FIGHTING, Fraction.ONE_HALF);
			get(DARK).put(GHOST, TWO);
			get(DARK).put(ELECTRIC, TWO);
			get(DARK).put(DARK, Fraction.ONE_HALF);
			get(DARK).put(FAIRY, Fraction.ONE_HALF);

			get(FAIRY).put(FIGHTING, TWO);
			get(FAIRY).put(POISON, Fraction.ONE_HALF);
			get(FAIRY).put(STEEL, Fraction.ONE_HALF);
			get(FAIRY).put(FIRE, Fraction.ONE_HALF);
			get(FAIRY).put(DRAGON, TWO);
			get(FAIRY).put(DARK, TWO);
		}

		protected Map<EElement, Fraction> get(final EElement element) {
			return map.getOrDefault(element, EMPTY_MAP);
		}
	}
}