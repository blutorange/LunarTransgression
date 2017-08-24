package com.github.blutorange.translune.logic;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.Fraction;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;

class BattleContext implements IBattleContext {
	protected static class DamageResult implements IDamageResult {

		int damage = 0;
		boolean isCritical = false;
		boolean isStab = false;
		ETypeEffectiveness typeEffectivness = ETypeEffectiveness.NORMALLY_EFFECTIVE;

		@Override
		public int getDamage() {
			return damage;
		}

		@Override
		public ETypeEffectiveness getTypeEffectiveness() {
			return typeEffectivness;
		}

		@Override
		public boolean isCritial() {
			return isCritical;
		}

		@Override
		public boolean isStab() {
			return isStab;
		}

		/**
		 * @return the typeEffectivness
		 */
		protected ETypeEffectiveness getTypeEffectivness() {
			return typeEffectivness;
		}

		/**
		 * @return the isCritical
		 */
		protected boolean isCritical() {
			return isCritical;
		}

		/**
		 * @param isCritical
		 *            the isCritical to set
		 */
		protected void setCritical(final boolean isCritical) {
			this.isCritical = isCritical;
		}

		/**
		 * @param damage
		 *            the damage to set
		 */
		protected void setDamage(final int damage) {
			this.damage = damage;
		}

		/**
		 * @param isStab
		 *            the isStab to set
		 */
		protected void setStab(final boolean isStab) {
			this.isStab = isStab;
		}

		/**
		 * @param typeEffectivness
		 *            the typeEffectivness to set
		 */
		protected void setTypeEffectivness(final ETypeEffectiveness typeEffectivness) {
			this.typeEffectivness = typeEffectivness;
		}
	}
	private static final Fraction THREE_HALFS = Fraction.getFraction(3, 2);
	private static final Fraction TWO = Fraction.getFraction(2, 1);
	private final List<BattleAction> battleActions1;
	private final List<BattleAction> battleActions2;
	private final BattleStatus[][] battleStatus;
	private final CharacterState[][] characterStates;
	private final IComputedBattleStatus[][] computedBattleStatus;
	private final List<IGlobalBattleEffector> effectorStack;
	private final IItemRemovable itemRemovable;
	private final List<String[]> items;

	private final String[] players;
	private final Random random;

	private final int turn;

	public BattleContext(final CharacterState[][] characterStates, final IComputedBattleStatus[][] computedBattleStatus,
			final List<String[]> items, final IItemRemovable itemRemovable, final BattleStatus[][] battleStatus,
			final List<IGlobalBattleEffector> effectorStack, final String[] players,
			final List<BattleAction> battleActions1, final List<BattleAction> battleActions2, final int turn) {
		this.computedBattleStatus = computedBattleStatus;
		this.items = items;
		this.characterStates = characterStates;
		this.turn = turn;
		this.battleStatus = battleStatus;
		this.effectorStack = effectorStack;
		this.battleActions1 = battleActions1;
		this.battleActions2 = battleActions2;
		this.itemRemovable = itemRemovable;
		this.players = players;
		random = ComponentFactory.getLogicComponent().randomBasic();
	}

	@Override
	public IDamageResult[] computeDamage(final Skill skill, final IComputedBattleStatus attacker,
			final IComputedBattleStatus... defenders) {
		final int modifierNumerator = 1;
		final int modifierDenominator = 1;

		Fraction modifier = Fraction.ONE;
		boolean isStab = false;

		// if the move has more than one target,
		if (defenders.length > 1)
			modifier = modifier.multiplyBy(Fraction.THREE_QUARTERS);

		// Same-type attack bonus. This is equal to 1.5 if the move's type
		// matches any
		// of the user's types, and 1 if otherwise.
		if (attacker.getCharacterState().getCharacter().getUnmodifiableElements().contains(skill.getElement())) {
			modifier = modifier.multiplyBy(THREE_HALFS);
			isStab = true;
		}

		// Halved if the attacker is burned and the used move is a physical move
		if (skill.getIsPhysical() && EStatusCondition.BURN.equals(attacker.getBattleStatus().getStatusConditions()))
			modifier.multiplyBy(Fraction.ONE_HALF);

		// Compute damage for each target
		final IDamageResult[] results = new IDamageResult[defenders.length];
		for (int i = 0; i < defenders.length; ++i) {
			final IComputedBattleStatus defender = defenders[i];
			final DamageResult result = new DamageResult();
			result.setStab(isStab);

			Fraction modifierSeparate = modifier;

			// 1.5 for a critical hit
			if (random.nextInt(100) > getCriticalHitThreshold(skill, attacker, defender)) {
				modifierSeparate = modifierSeparate.multiplyBy(THREE_HALFS);
				result.setCritical(true);
			}

			// Type effectiveness. This can be 0 (ineffective),
			// 0.25, 0.5 (not very effective), 1 (normally effective),
			// 2 or 4 (super effective) depending on both the move's
			// and target's types.
			final ETypeEffectiveness typeEffectiveness = computeTypeEffectiveness(skill, defender.getCharacterState());
			modifierSeparate = modifierSeparate.multiplyBy(typeEffectiveness.getMultiplier());
			result.setTypeEffectivness(typeEffectiveness);

			// Random factor between 0.85 and 1.00
			modifierSeparate = modifierSeparate.multiplyBy(Fraction.getFraction(random.nextInt(16) + 85, 100));

			// Attack and defense
			final int attack = skill.getIsPhysical() ? attacker.getComputedBattlePhysicalAttack()
					: attacker.getComputedBattleMagicalAttack();
			final int defense = skill.getIsPhysical() ? defender.getComputedBattlePhysicalDefense()
					: defender.getComputedBattleMagicalDefense();

			// Damage calculation
			final int damage = (((2 * attacker.getCharacterState().getLevel()) / 5 + 2) * skill.getPower() * attack
					/ defense) / 50
					+ 2 * modifierNumerator * modifierSeparate.getNumerator()
							/ (modifierDenominator * modifierSeparate.getDenominator());
			result.setDamage(damage);

			results[i] = result;
		}
		return results;
	}

	@Override
	public List<BattleAction> getBattleActions(final int player) {
		if (player == 0)
			return battleActions1;
		return battleActions2;
	}

	@Override
	public List<BattleAction> getBattleActionsOpponent(final int player) {
		if (player == 0)
			return battleActions2;
		return battleActions1;
	}

	@Override
	public BattleStatus[][] getBattleStatus() {
		return battleStatus;
	}

	@Override
	public BattleStatus[] getBattleStatus(final int player) {
		return battleStatus[player];
	}

	@Override
	public BattleStatus getBattleStatus(final int player, final int character) {
		return battleStatus[player][character];
	}

	@Override
	public BattleStatus getBattleStatus(final int[] characterIndex) {
		return getBattleStatus(characterIndex[0], characterIndex[1]);
	}

	@Override
	public int[] getCharacterIndex(final String character) {
		for (int player = 1; player-- > 0;)
			for (int c = 4; c-- > 0;)
				if (characterStates[player][c].getPrimaryKey().equals(character))
					return new int[] { player, c };
		throw new IllegalArgumentException("no such character");
	}

	@Override
	public CharacterState getCharacterState(final int player, final int character) {
		return characterStates[player][character];
	}

	@Override
	public CharacterState getCharacterState(final int[] characterIndex) {
		return getCharacterState(characterIndex[0], characterIndex[1]);
	}

	@Override
	public CharacterState[][] getCharacterStates() {
		return characterStates;
	}

	@Override
	public CharacterState[] getCharacterStates(final int player) {
		return characterStates[player];
	}

	@Override
	public IComputedBattleStatus[][] getComputedBattleStatus() {
		return computedBattleStatus;
	}

	@Override
	public IComputedBattleStatus[] getComputedBattleStatus(final int player) {
		return computedBattleStatus[player];
	}

	@Override
	public IComputedBattleStatus getComputedBattleStatus(final int player, final int character) {
		return computedBattleStatus[player][character];
	}

	@Override
	public IComputedBattleStatus getComputedBattleStatus(final int[] characterIndex) {
		return getComputedBattleStatus(characterIndex[0], characterIndex[1]);
	}

	@Override
	public List<IGlobalBattleEffector> getEffectorStack() {
		return effectorStack;
	}

	@Override
	public String getItem(final int player, final int item) {
		return items.get(player)[item];
	}

	@Override
	public List<String[]> getItems() {
		return items;
	}

	@Override
	public String[] getItems(final int player) {
		return items.get(player);
	}

	@Override
	public String getPlayer(final int player) {
		return players[player];
	}

	@Override
	public String[] getPlayers() {
		return players;
	}

	@Override
	public IComputedBattleStatus[] getTargetsAlive(final ITargettable targettable, final IBattleContext context,
			final BattleCommand battleCommand, final int player, final int character) {
		final IComputedBattleStatus[] targets = targettable.getTarget().getTargets(context, battleCommand, player,
				character);
		return Arrays.stream(targets).filter(target -> context
				.getComputedBattleStatus(context.getCharacterIndex(target.getCharacterState())).getComputedMaxHp() > 0)
				.toArray(IComputedBattleStatus[]::new);
	}

	@Override
	public int getTurn() {
		return turn;
	}

	@Override
	public void pushBattleEffector(final IGlobalBattleEffector effector) {
		effectorStack.add(effector);
		effector.onAdd(this);
	}

	@Override
	public boolean removeItem(final Item item, final int player) {
		final int index = ArrayUtils.indexOf(getItems(player), item.getPrimaryKey());
		if (index < 0)
			return false;
		itemRemovable.removeItem(player, item);
		ArrayUtils.remove(getItems(player), index);
		return true;
	}

	private ETypeEffectiveness computeTypeEffectiveness(final Skill skill, final CharacterState characterState) {
		final Set<EElement> elements = characterState.getCharacter().getUnmodifiableElements();
		final EElement element = skill.getElement();
		final Fraction multiplier = elements.stream().collect(Collectors.<EElement, Fraction> reducing(Fraction.ONE,
				e -> e.typeEffectivness(element), Fraction::multiplyBy)).reduce();
		if (multiplier.equals(Fraction.ONE))
			return ETypeEffectiveness.NORMALLY_EFFECTIVE;
		if (multiplier.equals(Fraction.ONE_HALF))
			return ETypeEffectiveness.NOT_VERY_EFFECTIVE;
		if (multiplier.equals(TWO))
			return ETypeEffectiveness.SUPER_EFFECTIVE;
		if (multiplier.equals(Fraction.ZERO))
			return ETypeEffectiveness.INEFFECTIVE;
		return ETypeEffectiveness.HYPER_EFFECTIVE;
	}

	private int getCriticalHitThreshold(final Skill skill, final IComputedBattleStatus attacker,
			final IComputedBattleStatus defender) {
		int threshold = 100 * attacker.getComputedBattleSpeed() / 512;
		if (skill.getHighCritical())
			threshold *= 8;
		return threshold;
	}
}