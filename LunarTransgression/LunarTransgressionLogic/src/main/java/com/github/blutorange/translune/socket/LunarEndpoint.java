package com.github.blutorange.translune.socket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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

import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.message.MessageUnknown;
import com.github.blutorange.translune.util.CustomProperties;

/**
 * <p>
 * Messages are processed in order of sending. To accomplish this, each message
 * contains an integer time code. This time code starts a zero (0) for the first
 * message and must increase monotonously for every next message. Messages sent
 * by the server and by the client have got a separate time code counter and do
 * not share the same time code counter. Additionally, response messages for the
 * client contain the time code of the original message so that client can
 * associate the response with the corresponding request.
 * </p>
 * A message itself it sent as a JSON object - this makes it easy to handle and
 * efficiency-wise, communications usually occur only at larger intervals.
 *
 * @author madgaksha
 */
@ServerEndpoint(value = "/ws/translune", encoders = LunarEncoder.class, decoders = LunarDecoder.class)
public class LunarEndpoint {
	@Inject
	@Classed(LunarEndpoint.class)
	Logger logger;

	@Inject
	ISocketProcessing socketProcessing;

	@Inject
	CustomProperties customProperties;

	public LunarEndpoint() {
		init();
	}

	private void init() {
		ComponentFactory.getLunarComponent().inject(this);
		logger.info("lunar endpoint created");
	}

	@OnOpen
	public void open(final Session session) throws IOException {
		if (!customProperties.isOnline()) {
			logger.debug("current not accepting connections, closing session");
			session.close(
					new CloseReason(CloseCodes.GOING_AWAY, "maintenance mode / currently not accepting connections"));
			return;
		}
		logger.debug("opening lunar session " + session.getId());
		final Set<Session> sessions = session.getOpenSessions();
		if (sessions.size() > 1) {
			logger.info("more than one session open to same endpoint, closing them");
			sessions.forEach(openSession -> {
				if (openSession.isOpen() && !openSession.getId().equals(session.getId()))
					socketProcessing.close(session, CloseCodes.PROTOCOL_ERROR, "New session from same user requested.");
			});
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
		if (logger.isDebugEnabled()) {
			logger.debug("receiving lunar message " + message.getTime() + " - session " + session.getId());
			logger.debug(message.toString());
		}
		// For the same session, only one message handler is called at the same
		// time.
		// In all cases, the implementation must not invoke an
		// endpoint instance with more than one thread per peer at a time.
		// - Java™ API for WebSocket, Chapter 5.1
		// However, for battles we need to synchronize on the session of
		// the opposing user.
		synchronized (session) {
			final AtomicInteger clientTime = socketProcessing.getClientTime(session);
			final Queue<LunarMessage> messageQueue = socketProcessing.getClientMessageQueue(session);
			// Remove very old messages.
			final long threshold = System.currentTimeMillis() - customProperties.getTimeoutMessageQueueMillis();
			messageQueue.removeIf(msg -> msg.getReceptionTime() < threshold);
			// Process messages in order.
			messageQueue.add(message);
			LunarMessage currentMessage;
			while ((currentMessage = messageQueue.peek()) != null && currentMessage.getTime() == clientTime.get()) {
				try {
					processMessage(session, messageQueue.poll());
				}
				catch (final Exception throwable) {
					error(session, throwable);
				}
				finally {
					clientTime.incrementAndGet();
				}
			}
		}
	}

	@OnClose
	public void close(final Session session, final CloseReason closeReason) {
		if (logger.isDebugEnabled()) {
			logger.debug("closing lunar session " + session.getId());
			logger.debug(String.format("CloseReason: %s %s", Integer.valueOf(closeReason.getCloseCode().getCode()),
					closeReason.getReasonPhrase()));
		}
		socketProcessing.finalizeSession(session);
	}

	@OnError
	public void error(final Session session, final Throwable throwable) {
		logger.error("error occured during socket communication: " + session.getId(), throwable);
	}

	private void handleAuthorization(final Session session, final LunarMessage message) {
		if (message.getType() != ELunarMessageType.AUTHORIZE) {
			logger.debug("not authorized, discarding message");
			socketProcessing.dispatchMessage(session, ELunarStatusCode.ACCESS_DENIED, new MessageUnknown());
			return;
		}
		logger.debug("not authorized, validating credentials");
		ELunarMessageType.AUTHORIZE.handle(session, message);
	}

	private void processMessage(final Session session, final LunarMessage message) {
		if (logger.isDebugEnabled()) {
			logger.debug("processing lunar message " + message.getTime() + " - session " + session.getId());
			logger.debug(message.toString());
		}
		if (socketProcessing.isAuthorized(session)) {
			message.getType().handle(session, message);
		}
		else {
			handleAuthorization(session, message);
		}
	}
}