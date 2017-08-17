package com.github.blutorange.translune.handler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.LunarMessage;

@Singleton
public class HandlerPrepareBattle implements ILunarMessageHandler {
	@Classed(HandlerPrepareBattle.class)
	@Inject
	protected Logger logger;

	@Inject
	public HandlerPrepareBattle() {}

	@Override
	public void handle(final String user, final Session session, final LunarMessage message) {
		// TODO implement me
		throw new RuntimeException("not yet implemented");
	}
}