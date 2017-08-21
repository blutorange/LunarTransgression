package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.socket.BattleCommand;

public interface IBattleRunner extends Runnable {
	void preparePlayer(String nickname, String[] characterStates, String[] items)
			throws IllegalStateException, IllegalArgumentException;

	void forPlayers(String from, String to);

	void battlePlayer(String nickname, BattleCommand character1, BattleCommand character2, BattleCommand character3,
			BattleCommand character4) throws IllegalStateException, IllegalArgumentException;
}