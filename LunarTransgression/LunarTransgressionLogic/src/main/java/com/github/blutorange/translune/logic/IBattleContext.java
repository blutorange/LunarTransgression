package com.github.blutorange.translune.logic;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.socket.BattleAction;

public interface IBattleContext {
	List<BattleAction> getBattleActions(int player);

	List<BattleAction> getBattleActionsOpponent(int player);

	IBattleStatus[][] getBattleStatus();

	IBattleStatus[] getBattleStatus(int player);

	BattleStatus getBattleStatus(int player, int character);

	IBattleStatus getBattleStatus(int[] characterIndex);

	public CharacterStatsDelta[] differenceToLast();

	default int[] getCharacterIndex(final CharacterState character) {
		return getCharacterIndex(character.getPrimaryKey().toString());
	}

	default int[] getCharacterIndex(final IComputedStatus character) {
		return getCharacterIndex(character.getCharacterState().getPrimaryKey().toString());
	}

	int[] getCharacterIndex(String character);

	CharacterState getCharacterState(int player, int character);

	CharacterState getCharacterState(int[] characterIndex);

	CharacterState[][] getCharacterStates();

	CharacterState[] getCharacterStates(int player);

	default CharacterState[] getCharacterStatesOpponent(final int player) {
		final int opponent = player == 0 ? 1 : 0;
		return getCharacterStates(opponent);
	}

	IComputedBattleStatus[][] getComputedBattleStatus();

	IComputedBattleStatus[] getComputedBattleStatus(int player);

	IComputedBattleStatus getComputedBattleStatus(int player, int character);

	IComputedBattleStatus getComputedBattleStatus(int[] characterIndex);

	default IComputedBattleStatus[] getComputedBattleStatusOpponent(final int player) {
		return getComputedBattleStatusOpponent(player == 0 ? 1 : 0);
	}

	List<IGlobalBattleEffector> getEffectorStack();

	@Nullable
	String getItem(int player, int item);

	List<String[]> getItems();

	String[] getItems(int player);

	String getPlayer(int player);

	String[] getPlayers();

	int getTurn();

	void pushBattleEffector(IGlobalBattleEffector effector);

	boolean removeItem(Item item, final int player);
}