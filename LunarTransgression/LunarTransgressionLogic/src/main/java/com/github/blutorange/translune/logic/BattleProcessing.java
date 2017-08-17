package com.github.blutorange.translune.logic;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BattleProcessing implements IBattleProcessing {
	@Inject
	public BattleProcessing() {

	}

	@Override
	public IBattleResult[][] simulateBattleStep(final List<IBattleCommand[]> commands, final String[] players,
			final List<String[]> characterStates, final List<String[]> items) {
		// TODO implement me
		throw new RuntimeException("TODO - not yet implemented");
	}
}