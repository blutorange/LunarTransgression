package com.github.blutorange.translune.logic;

import javax.inject.Inject;

public class Battle {
	private final int round = 1;
	private final long roundStart = System.currentTimeMillis();
	String from = "";
	String to = "";
	private final ISessionStore sessionStore;

	@Inject
	public Battle(final ISessionStore sessionStore) {
		this.sessionStore = sessionStore;
	}

	public void forPlayers(final String from, final String to) {
		this.from = from;
		this.to= to;
	}

}
