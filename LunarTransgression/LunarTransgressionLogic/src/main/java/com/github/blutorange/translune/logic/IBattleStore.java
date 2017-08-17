package com.github.blutorange.translune.logic;

public interface IBattleStore {
	void startBattle(String from, String to);
	void removeBattle(String from, String to);
}