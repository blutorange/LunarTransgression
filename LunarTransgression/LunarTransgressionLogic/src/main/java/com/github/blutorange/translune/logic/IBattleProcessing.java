package com.github.blutorange.translune.logic;

import java.util.List;

public interface IBattleProcessing {
	/**
	 *
	 * @param commands
	 * @param players
	 * @param characterStates
	 * @param items
	 * @return IBattleResult[playerIndex=0..1][characterIndex=0...3]
	 */
	IBattleAction[][] simulateBattleStep(List<IBattleCommand[]> commands, String[] players,
			List<String[]> characterStates, List<String[]> items);

	/**
	 *
	 * @param characterStates
	 * @return <0 iff there is no winner yet, 0 iff it is player 1, 1 iff it is player 2.
	 */
	int checkBattleEnd(BattleStatus[][] characterStates);
}