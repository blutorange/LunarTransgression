package com.github.blutorange.translune.logic;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.ILunarDatabaseManager;

@Singleton
public class BattleProcessing implements IBattleProcessing {
	@Inject
	ILunarDatabaseManager databaseManager;

	@Inject
	public BattleProcessing() {
		
	}

	public IComputedStatus getComputedStatus(final CharacterState characterState, final BattleStatus status) {
		
	}

	public IComputedStatus getComputedStatus(final String characterState, final BattleStatus status) {
		return getComputedStatus(databaseManager.find(CharacterState.class), characterState);
	}

	@Override
	public IBattleAction[][] simulateBattleStep(final List<IBattleCommand[]> commands, final String[] players,
			final List<String[]> characterStates, final List<String[]> items) {
		// TODO implement me
		throw new RuntimeException("TODO - not yet implemented");
	}

	@Override
	public int checkBattleEnd(final BattleStatus[][] battleStatus) {
		if (getKnockoutCount(battleStatus[0]) >= 2)
			return 0;
		if (getKnockoutCount(battleStatus[1]) >= 2)
			return 1;
		return -1;
	}

	private int getKnockoutCount(final BattleStatus[] battleStatus) {
		int count = 0;
		for (final BattleStatus bs : battleStatus) {
			if (bs.getHp() <= 0)
				++count;
		}
		return count;
	}
}