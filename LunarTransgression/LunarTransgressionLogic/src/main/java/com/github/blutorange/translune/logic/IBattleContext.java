package com.github.blutorange.translune.logic;

import java.util.List;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.socket.BattleAction;

public interface IBattleContext {

	IComputedBattleStatus[][] getComputedBattleStatus();

	IComputedBattleStatus[] getComputedBattleStatus(int player);

	IComputedBattleStatus getComputedBattleStatus(int player, int character);

	List<String[]> getItems();

	String[] getItems(int player);

	String getItem(int player, int item);

	CharacterState[] getCharacterStates(int player);

	CharacterState[] getCharacterStatesOpponent(int player);

	CharacterState[][] getCharacterStates();

	CharacterState getCharacterState(int player, int character);

	BattleStatus[][] getBattleStatus();

	BattleStatus[] getBattleStatus(int player);

	BattleStatus getBattleStatus(int player, int character);

	void pushBattleEffector(IGlobalBattleEffector effector);

	String[] getPlayers();

	String getPlayer(int player);

	int getTurn();

	boolean removeItem(Item item, final int player);

	int[] getCharacterIndex(String character);

	BattleStatus getBattleStatus(int[] characterIndex);

	IComputedBattleStatus getComputedBattleStatus(int [] characterIndex);

	CharacterState getCharacterState(int[] characterIndex);

	List<BattleAction> getBattleAction(int player);

	List<IGlobalBattleEffector> getEffectorStack();
}