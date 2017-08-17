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
	IBattleResult[][] simulateBattleStep(List<IBattleCommand[]> commands, String[] players,
			List<String[]> characterStates, List<String[]> items);
}