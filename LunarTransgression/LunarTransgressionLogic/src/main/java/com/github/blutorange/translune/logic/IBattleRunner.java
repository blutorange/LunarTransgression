package com.github.blutorange.translune.logic;

public interface IBattleRunner extends Runnable {
	void preparePlayer(String nickname, String[] characterStates, String[] items)
			throws IllegalStateException, IllegalArgumentException;

	void forPlayers(String from, String to);

	void battlePlayer(String nickname, IBattleCommand character1, IBattleCommand character2, IBattleCommand character3,
			IBattleCommand character4) throws IllegalStateException, IllegalArgumentException;
}