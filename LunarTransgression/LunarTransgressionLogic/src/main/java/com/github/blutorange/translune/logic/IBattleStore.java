package com.github.blutorange.translune.logic;

import org.eclipse.jdt.annotation.Nullable;

public interface IBattleStore {
	void startBattle(String from, String to);
	void removeBattle(String from, String to);
	@Nullable IBattleRunner retrieve(String nickname);
	void addLoot(String player, String[] characterStates, String[] items);
	@Nullable ILootable getLoot(String player);
	void removeLoot(String player);
}