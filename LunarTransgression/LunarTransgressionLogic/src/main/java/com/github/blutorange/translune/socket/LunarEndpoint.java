package com.github.blutorange.translune.socket;

import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.github.blutorange.translune.Constants;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.InjectionUtil;
import com.github.blutorange.translune.logic.IInitIdStore;
import com.github.blutorange.translune.logic.ISessionStore;

@ServerEndpoint(value = "/ws/translune", encoders = LunarEncoder.class, decoders = LunarDecoder.class)
public class LunarEndpoint {
	@Inject
	@Classed(LunarEndpoint.class)
	Logger logger;

	@Inject
	IInitIdStore initIdStore;

	@Inject
	ISessionStore sessionStore;

	@Inject
	ISocketProcessing socketProcessing;

	public LunarEndpoint() {
		InjectionUtil.inject(this);
	}

	@OnOpen
	public void open(final Session session) {
		if (logger.isDebugEnabled()) {
			logger.debug("opening lunar session " + session.getId());
		}
		if (session.getOpenSessions().size() > 1) {
			logger.info("more than one session open to same endpoint, closing: " + session.getId());
			socketProcessing.close(session, CloseCodes.VIOLATED_POLICY, "Only one session may be opened");
			return;
		}
		session.getUserProperties().put(Constants.SESSION_KEY_STARTED, Long.valueOf(System.currentTimeMillis()));
	}

	@OnMessage
	public void message(final Session session, final LunarMessage message) {
		if (logger.isDebugEnabled()) {
			logger.debug("receiving lunar message " + session.getId());
			logger.debug(message.toString());
		}
		if (session.getUserProperties().containsKey(Constants.SESSION_KEY_AUTHORIZED)) {
			final String payload = message.getPayload();
			message.getType().handle(session, payload != null ? payload : StringUtils.EMPTY);
		}
		else {
			handleAuthorization(session, message);
		}
	}

	private void handleAuthorization(final Session session, final LunarMessage message) {
		if (message.getType() != ELunarMessageType.AUTHORIZE) {
			socketProcessing.close(session, CloseCodes.CANNOT_ACCEPT, "first message must be authorization");
			return;
		}
		final String payload = message.getPayload();
		ELunarMessageType.AUTHORIZE.handle(session, payload != null ? payload : StringUtils.EMPTY);
	}

	@OnClose
	public void close(final Session session, final CloseReason closeReason) {
		if (logger.isDebugEnabled()) {
			logger.debug("closing lunar session " + session.getId());
			logger.debug(
					String.format("CloseReason: %d %s", closeReason.getCloseCode(), closeReason.getReasonPhrase()));
		}
		final String nickname = (String)(session.getUserProperties().get(Constants.SESSION_KEY_NICKNAME));
		if (nickname != null && !nickname.isEmpty())
			sessionStore.remove(nickname);
		else
			sessionStore.remove(session);
	}

	@OnError
	public void error(final Session session, final Throwable throwable) {
		logger.error("error occured during socket communication: " + session.getId(), throwable);
	}
}