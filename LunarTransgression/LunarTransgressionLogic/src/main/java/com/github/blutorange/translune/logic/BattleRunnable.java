package com.github.blutorange.translune.logic;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;

import com.github.blutorange.translune.CustomProperties;
import com.github.blutorange.translune.db.IEntityStore;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.Classed;

public class BattleRunnable implements Runnable {
	private int round;
	private final long roundStart;
	
	String[] players = new String[2];
	
	List<String@Nullable[]> characters;
	List<@Nullable String[]> items;

	
	@Nullable
	private CountDownLatch latch;

	@Inject
	@Classed(BattleRunnable.class)
	Logger logger;

	@Inject
	CustomProperties customProperties;

	@Inject
	ISessionStore sessionStore;

	@Inject
	IEntityStore entityStore;

	@Inject
	public BattleRunnable() {
		round = 0;
		roundStart = System.currentTimeMillis();
		characters = new CopyOnWriteArrayList<>(new String[2][]);
		items = new CopyOnWriteArrayList<>(new String[2][]);		
	}

	public void forPlayers(final String from, final String to) {
		players[0] = from;
		players[1] = to;
	}

	@Override
	public void run() {
		if (round == 0) {
			battlePreparations();
		}
		++round;
	}
	
	public void preparePlayer(final String nickname, final String[] characters, final String[] items) throws IllegalStateException, IllegalArgumentException {
		final CountDownLatch latch = this.latch;
		if (latch == null)
			throw new IllegalStateException("Battle not initialized yet");
		final int playerIndex = getPlayerIndex(nickname);
		if (this.characters.get(playerIndex) != null)
			throw new IllegalStateException("Player is already prepared");
		if (characters.length != 4)
			throw new IllegalArgumentException("Player must choose exactly 4 characters.");
		if (items.length > 3)
			throw new IllegalArgumentException("Player must not choose more than 3 items.");
		assertCharacters(playerIndex, characters);
		this.characters.set(playerIndex, characters);
		this.items.set(playerIndex, items);
		latch.countDown();
	}

	private void assertCharacters(final int playerIndex, final String[] characters, final String[] items) {
		final Player player = entityStore.retrieve(Player.class, players[playerIndex]);
		for (final String character : characters)
			if (!player.containsCharacter(character))
				throw new IllegalArgumentException("Player does not own this character: " + character);
		for (final String item : items)
			if (!player.containsItem(item))
				throw new IllegalArgumentException("Player does not own this item: " + item);
	}

	private int getPlayerIndex(final String nickname) {
		if (players[0].equals(nickname)) return 0;
		if (players[1].equals(nickname)) return 1;
		throw new IllegalArgumentException("Player does not participate in this battle: " + nickname);
	}

	private void battlePreparations() {
		latch = new CountDownLatch(2);
		try {
			latch.await(customProperties.getBattlePreparationTimeoutMillis(), TimeUnit.MILLISECONDS);
		}
		catch (final InterruptedException e) {
			logger.error("battle preparations interrupted", e);
			handleInterrupt();
		}
		// TODO start battle
		
//		synchronized (this) {
//			while ("<condition does not hold>" != null) {
//				try {
//					wait(customProperties.getBattlePreparationTimeoutMillis());
//				}
//				catch (final InterruptedException e) {
//					logger.warn("interrupt while waiting for battle preparation input", e);
//				}
//			}
//		}
	}
}