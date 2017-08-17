package com.github.blutorange.translune.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.ComponentFactory;

@Singleton
public class BattleStore implements IBattleStore {
	@Inject @Classed(BattleStore.class)
	Logger logger;

	@Inject
	ExecutorService executorService;

	Map<String, IBattleRunner> fromBattleMap;
	Map<String, String> toFromMap;

	@Inject
	public BattleStore() {
		fromBattleMap = new HashMap<>();
		toFromMap = new HashMap<>();
	}

	@Override
	public void startBattle(final String from, final String to) {
		synchronized(this) {
			final IBattleRunner battle = ComponentFactory.createLogicComponent().battleRunner();
			battle.forPlayers(from, to);
			executorService.submit(battle);
			toFromMap.put(to, from);
			fromBattleMap.put(from, battle);
		}
	}

	@Override
	public void removeBattle(final String from, final String to) {
		synchronized(this) {
			fromBattleMap.remove(from);
			toFromMap.remove(to);
		}
	}
}