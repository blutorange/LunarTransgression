package com.github.blutorange.translune.logic;

import java.util.List;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;

public interface IBattleContext {
	List<BattleAction> getBattleActions(int player);

	List<BattleAction> getBattleActionsOpponent(int player);

	BattleStatus[][] getBattleStatus();

	BattleStatus[] getBattleStatus(int player);

	BattleStatus getBattleStatus(int player, int character);

	BattleStatus getBattleStatus(int[] characterIndex);

	int[] getCharacterIndex(String character);

	CharacterState getCharacterState(int player, int character);

	CharacterState getCharacterState(int[] characterIndex);

	CharacterState[][] getCharacterStates();

	CharacterState[] getCharacterStates(int player);

	IComputedBattleStatus[][] getComputedBattleStatus();

	IComputedBattleStatus[] getComputedBattleStatus(int player);

	IComputedBattleStatus getComputedBattleStatus(int player, int character);

	IComputedBattleStatus getComputedBattleStatus(int[] characterIndex);

	List<IGlobalBattleEffector> getEffectorStack();

	String getItem(int player, int item);

	List<String[]> getItems();

	String[] getItems(int player);

	String getPlayer(int player);

	String[] getPlayers();

	int getTurn();

	void pushBattleEffector(IGlobalBattleEffector effector);

	boolean removeItem(Item item, final int player);

	IComputedBattleStatus[] getTargetsAlive(ITargettable targettable, IBattleContext context,
			BattleCommand battleCommand, int player, int character);

	IDamageResult[] computeDamage(Skill skill, IComputedBattleStatus attacker, IComputedBattleStatus[] defenders);

	default CharacterState[] getCharacterStatesOpponent(final int player) {
		final int opponent = player == 0 ? 1 : 0;
		return getCharacterStates(opponent);
	}

	default int[] getCharacterIndex(final CharacterState target) {
		return getCharacterIndex(target.getPrimaryKey().toString());
	}

	default IComputedBattleStatus[] getComputedBattleStatusOpponent(final int player) {
		return getComputedBattleStatusOpponent(player == 0 ? 1 : 0);
	}
}