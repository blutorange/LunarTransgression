package com.github.blutorange.translune.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.DaggerLogicComponent;

@Singleton
public class BattleStore implements IBattleStore {
	@Inject @Classed(BattleStore.class)
	Logger logger;
	
	@Inject
	ExecutorService executorService;
	
	Map<String, BattleRunnable> fromBattleMap;
	Map<String, String> toFromMap;

	@Inject
	public BattleStore() {
		fromBattleMap = new HashMap<>();
		toFromMap = new HashMap<>();
	}

	@Override
	public void addNewBattle(final String from, final String to) {
		synchronized(this) {
			final BattleRunnable battle = DaggerLogicComponent.create().battle();
			battle.forPlayers(from, to);
			executorService.submit(battle);
			toFromMap.put(to, from);
			fromBattleMap.put(from, battle);
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		throw new RuntimeException("not implemented");
	}
}