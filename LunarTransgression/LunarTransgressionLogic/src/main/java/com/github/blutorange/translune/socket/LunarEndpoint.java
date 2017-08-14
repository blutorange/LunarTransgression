package com.github.blutorange.translune.socket;

import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.InjectionUtil;
import com.github.blutorange.translune.logic.IInitIdStore;
import com.github.blutorange.translune.logic.ISessionStore;
import com.github.blutorange.translune.socket.message.MessageUnknown;

// TODO establish order
// only one message method for a user may be active at a time
// message processing must be in order (of sending)
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

	@PostConstruct
	private void init() {
		InjectionUtil.inject(this);
	}

	@OnOpen
	public void open(final Session session) {
		if (logger.isDebugEnabled()) {
			logger.debug("opening lunar session " + session.getId());
		}
		if (session.getOpenSessions().size() > 1) {
			logger.info("more than one session open to same endpoint, closing: " + session.getId());
			socketProcessing.close(session, CloseCodes.VIOLATED_POLICY, "Only one session may be opened.");
			return;
		}
		socketProcessing.initSession(session);
	}

	@OnMessage
	public void message(final Session session, final PongMessage pongMessage) {
		final String response = new String(pongMessage.getApplicationData().array(), StandardCharsets.UTF_8);
		socketProcessing.dispatchMessage(session, String.format("%s: pong received: %s", session.getId(), response));
	}

	@OnMessage
	public void message(final Session session, final LunarMessage message) {
		//final boolean test = message.getTime() >= ((AtomicInteger)(session.getUserProperties().get(Constants.SESSION_KEY_CLIENT_TIME))).get() + 1 ;
		if (logger.isDebugEnabled()) {
			logger.debug("receiving lunar message " + session.getId());
			logger.debug(message.toString());
		}
		if (socketProcessing.isAuthorized(session)) {
			final String payload = message.getPayload();
			message.getType().handle(session, payload != null ? payload : StringUtils.EMPTY);
		}
		else {
			handleAuthorization(session, message);
		}
	}

	@OnClose
	public void close(final Session session, final CloseReason closeReason) {
		//TODO
		// Remove open invitations and inform the other user
		if (logger.isDebugEnabled()) {
			logger.debug("closing lunar session " + session.getId());
			logger.debug(
					String.format("CloseReason: %d %s", closeReason.getCloseCode(), closeReason.getReasonPhrase()));
		}
		final String nickname = socketProcessing.getNickname(session);
		if (!nickname.isEmpty())
			sessionStore.remove(nickname);
		else
			sessionStore.remove(session);
	}

	@OnError
	public void error(final Session session, final Throwable throwable) {
		logger.error("error occured during socket communication: " + session.getId(), throwable);
	}

	private void handleAuthorization(final Session session, final LunarMessage message) {
		if (message.getType() != ELunarMessageType.AUTHORIZE) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.ACCESS_DENIED, MessageUnknown.INSTANCE);
			return;
		}
		final String payload = message.getPayload();
		ELunarMessageType.AUTHORIZE.handle(session, payload != null ? payload : StringUtils.EMPTY);
	}
}