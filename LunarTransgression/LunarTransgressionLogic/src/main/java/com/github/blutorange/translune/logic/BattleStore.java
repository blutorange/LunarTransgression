package com.github.blutorange.translune.logic;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.DaggerLogicComponent;

public class BattleStore implements IBattleStore {
	private final Logger logger;

	Map<String, Battle> fromBattleMap;
	Map<String, String> toFromMap;

	public BattleStore(@Classed(BattleStore.class) final Logger logger) {
		this.logger = logger;
		fromBattleMap = new HashMap<>();
		toFromMap = new HashMap<>();
	}

	@Override
	public void addNewBattle(@NonNull final String from, @NonNull final String to) {
		synchronized(this) {
			final Battle battle = DaggerLogicComponent.create().battle();
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
