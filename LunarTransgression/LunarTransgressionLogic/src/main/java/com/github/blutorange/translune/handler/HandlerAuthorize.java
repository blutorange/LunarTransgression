package com.github.blutorange.translune.handler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.EGameState;
import com.github.blutorange.translune.logic.IInitIdStore;
import com.github.blutorange.translune.logic.ISessionStore;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.message.MessageAuthorize;
import com.github.blutorange.translune.socket.message.MessageAuthorizeResponse;

@Singleton
public class HandlerAuthorize implements ILunarMessageHandler {
	@Inject
	protected ISessionStore sessionStore;
	@Inject
	protected IInitIdStore initIdStore;
	@Classed(HandlerAuthorize.class)
	@Inject
	protected Logger logger;
	@Inject
	protected ISocketProcessing socketProcessing;

	@Inject
	public HandlerAuthorize() {}

	@Override
	public void handle(final String user, final Session session, final String payload) {
		final MessageAuthorize msg = socketProcessing.getMessage(payload, MessageAuthorize.class);
		if (msg == null || !initIdStore.assertAndClear(msg.getNickname(), msg.getInitId())) {
			logger.info("found invalid credentials: " + session.getId());
			socketProcessing.close(session, CloseCodes.CANNOT_ACCEPT, "invalid credentials");
			return;
		}
		final String nickname = msg.getNickname();
		sessionStore.store(msg.getNickname(), session, oldSession -> {
			if (oldSession != null && oldSession.isOpen()) {
				socketProcessing.transfer(oldSession, session);
				socketProcessing.close(oldSession, CloseCodes.UNEXPECTED_CONDITION,
						"closing because new connection was requested");
			}
			socketProcessing.setNickname(session, nickname);
			socketProcessing.markAuthorized(session);
			socketProcessing.setGameState(session, EGameState.IN_MENU);
		});
		socketProcessing.dispatchMessage(session, ELunarStatusCode.OK, new MessageAuthorizeResponse());
	}
}