package com.github.blutorange.translune.socket;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.InjectionUtil;

@ServerEndpoint(value="/ws/test")
public class TestEndpoint {
	@Inject @Classed(TestEndpoint.class) Logger logger;

	private static AtomicInteger c = new AtomicInteger(0);
	private int id = 0;

	public TestEndpoint() {
		id = c.incrementAndGet();
		InjectionUtil.inject(this);
	}

	@OnOpen
	public void open(final Session session) {
		logger.debug("session: " + session.getId());
		logger.debug("opening test endpoint");
	}

	@OnMessage
	public void message(final Session session, final String message) {
		logger.debug("Test: " + id);
		logger.debug("session: " + session.getId());
		logger.debug("receiving message on test endpoint " + message);
		if (message.equalsIgnoreCase("ping")) {
			try {
				session.getBasicRemote().sendText("pong");
			}
			catch (final IOException e) {
				logger.error("failed to write response", e);
			}
		}
	}

	@OnClose
	public void close(final Session session, final CloseReason closeReason) {
		logger.debug("session: " + session.getId());
		logger.debug("closing test endpoint: " + closeReason.getReasonPhrase());
		logger.debug("");
	}

	@OnError
	public void error(final Session session, final Throwable throwable) {
		logger.debug("session: " + session.getId());
		logger.error("Error in test endpoint", throwable);
	}
}
