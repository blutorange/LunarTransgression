package com.github.blutorange.translune.socket.handler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import org.slf4j.Logger;

import com.github.blutorange.translune.Constants;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.IInitIdStore;
import com.github.blutorange.translune.logic.ISessionStore;
import com.github.blutorange.translune.socket.ISocketHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.message.MessageAuthorize;
import com.jsoniter.JsonIterator;

@Singleton
public class HandlerAuthorize implements ISocketHandler {
	private final ISessionStore sessionStore;
	private final IInitIdStore initIdStore;
	private final Logger logger;
	private final ISocketProcessing socketProcessing;

	@Inject
	public HandlerAuthorize(@Classed(HandlerAuthorize.class) final Logger logger, final IInitIdStore initIdStore,
			final ISessionStore sessionStore, final ISocketProcessing socketProcessing) {
		this.initIdStore = initIdStore;
		this.sessionStore = sessionStore;
		this.logger = logger;
		this.socketProcessing = socketProcessing;
	}

	@SuppressWarnings("resource")
	@Override
	public void handle(final String user, final Session session, final String payload) {
		MessageAuthorize msg = null;
		try {
			msg = JsonIterator.deserialize(payload, MessageAuthorize.class);
		}
		catch (final Exception e) {
			logger.error("failed to process authorization message", e);
		}
		if (msg == null || !initIdStore.assertAndClear(msg.getNickname(), msg.getInitId())) {
			logger.info("user was not authorized ");
			socketProcessing.close(session, CloseCodes.CANNOT_ACCEPT, "not authorized");
			return;
		}
		final Session activeSession = sessionStore.store(msg.getNickname(), session);
		if (activeSession != null && activeSession.isOpen()) {
			socketProcessing.close(activeSession, CloseCodes.UNEXPECTED_CONDITION,
					"closing because new connection was requested");
		}
		session.getUserProperties().put(Constants.SESSION_KEY_NICKNAME, msg.getNickname());
		session.getUserProperties().put(Constants.SESSION_KEY_AUTHORIZED, null);
	}
}