package com.github.blutorange.translune.logic;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.socket.BattleAction;

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

	private final List<BattleAction> battleActions1;
	private final List<BattleAction> battleActions2;
	private final BattleStatus[][] battleStatus;
	private final CharacterState[][] characterStates;
	private final IComputedBattleStatus[][] computedBattleStatus;
	private final List<IGlobalBattleEffector> effectorStack;
	private final IItemRemovable itemRemovable;
	private final List<String[]> items;

	private final String[] players;

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
		for (int player = 2; player-- > 0;)
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

	@Nullable
	@Override
	public String getItem(final int player, final int item) {
		return getItems(player)[item];
	}

	@Override
	public List<String[]> getItems() {
		return items;
	}

	@Override
	public String[] getItems(final int player) {
		final String[] items = this.items.get(player);
		if (items == null)
			throw new RuntimeException("Items array is null for player " + player);
		return items;
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
}