package com.github.blutorange.translune.logic;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.socket.BattleAction;

class BattleContext implements IBattleContext {
	private final IComputedBattleStatus[][] computedBattleStatus;
	private final CharacterState[][] characterStates;
	private final List<String[]> items;
	private final BattleStatus[][] battleStatus;
	private final int turn;
	private final List<IGlobalBattleEffector> effectorStack;
	private final String[] players;
	private final List<BattleAction> battleActions1;
	private final List<BattleAction> battleActions2;

	public BattleContext(final CharacterState[][] characterStates, final IComputedBattleStatus[][] computedBattleStatus,
			final List<String[]> items, final BattleStatus[][] battleStatus, final List<IGlobalBattleEffector> effectorStack,
			final String[] players, final List<BattleAction> battleActions1, final List<BattleAction> battleActions2, final int turn) {
		this.computedBattleStatus = computedBattleStatus;
		this.items = items;
		this.characterStates = characterStates;
		this.turn = turn;
		this.battleStatus = battleStatus;
		this.effectorStack = effectorStack;
		this.battleActions1 = battleActions1;
		this.battleActions2 = battleActions2;
		this.players = players;
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
	public CharacterState[] getCharacterStatesOpponent(final int player) {
		final int opponent = player == 0 ? 1 : 0;
		return getCharacterStates(opponent);
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
	public void pushBattleEffector(final IGlobalBattleEffector effector) {
		effectorStack.add(effector);
		effector.onAdd(this);
	}

	@Override
	public List<IGlobalBattleEffector> getEffectorStack() {
		return effectorStack;
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
	public String getPlayer(final int player) {
		return players[player];
	}

	@Override
	public boolean removeItem(final Item item, final int player) {
		final int index = ArrayUtils.indexOf(getItems(player), item.getPrimaryKey());
		if (index < 0)
			return false;
		ArrayUtils.remove(getItems(player), index);
		return true;
	}

	@Override
	public int[] getCharacterIndex(final String character) {
		for (int player = 1; player --> 0;)
			for (int c = 4; c --> 0;)
				if (characterStates[player][c].getPrimaryKey().equals(character))
					return new int[]{player,c};
		throw new IllegalArgumentException("no such character");
	}

	@Override
	public List<BattleAction> getBattleAction(final int player) {
		if (player == 0)
			return battleActions1;
		return battleActions2;
	}

	@Override
	public int[] getCharacterIndex(CharacterState target) {
		return getCharacterIndex(target.getPrimaryKey().toString());
	}
}