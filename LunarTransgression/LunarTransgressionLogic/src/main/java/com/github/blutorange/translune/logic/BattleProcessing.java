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

	@Override
	public IBattleAction[][] simulateBattleStep(final List<IBattleCommand[]> commands, final String[] players,
			final List<String[]> characterStates, final List<String[]> items) {
		// TODO implement me
		throw new RuntimeException("TODO - not yet implemented");
	}

	@Override
	public int checkBattleEnd(final List<String[]> characterStates) {
		final CharacterState[] states1 = databaseManager.findAll(CharacterState.class, characterStates.get(0));
		final CharacterState[] states2 = databaseManager.findAll(CharacterState.class, characterStates.get(1));
		if (getKnockoutCount(states1) >= 2)
			return 0;
		if (getKnockoutCount(states2) >= 2)
			return 1;
		return -1;
	}

	private int getKnockoutCount(final CharacterState[] characterStates) {
		int count = 0;
		for (final CharacterState characterState : characterStates) {
			if (characterState.getHp() <= 0)
				++count;
		}
		return count;
	}
}