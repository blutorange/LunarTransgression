package com.github.blutorange.translune.logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.ComponentFactory;

@Singleton
public class BattleStore implements IBattleStore {
	@Inject @Classed(BattleStore.class)
	Logger logger;

	@Inject
	ExecutorService executorService;

	Map<String, ILootable> lootMap;
	Map<String, IBattleRunner> fromBattleMap;
	Map<String, String> toFromMap;

	@Inject
	public BattleStore() {
		lootMap = Collections.synchronizedMap(new HashMap<>());
		fromBattleMap = new HashMap<>();
		toFromMap = new HashMap<>();
	}

	@Override
	public void startBattle(final String from, final String to) {
		synchronized(this) {
			final IBattleRunner battle = ComponentFactory.getLunarComponent().battleRunner();
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

	@Override
	@Nullable
	public IBattleRunner retrieve(final String nickname) {
		final String from = toFromMap.getOrDefault(nickname, nickname);
		return fromBattleMap.get(from);
	}

	@Override
	public void addLoot(final String player, final String[] characterStates, final String[] items) {
		lootMap.put(player, new LootImpl(characterStates, items));
	}

	@Override
	@Nullable
	public ILootable getLoot(final String player) {
		return lootMap.get(player);
	}

	@Override
	public void removeLoot(final String player) {
		lootMap.remove(player);
	}

	protected static class LootImpl implements ILootable {
		private final String[] characterStates;
		private final String[] items;

		public LootImpl(final String[] characterStates, final String[] items) {
			this.characterStates = characterStates;
			this.items = items;
		}

		@Override
		public String[] getCharacterStates() {
			return characterStates;
		}

		@Override
		public String[] getItems() {
			return items;
		}
	}
}