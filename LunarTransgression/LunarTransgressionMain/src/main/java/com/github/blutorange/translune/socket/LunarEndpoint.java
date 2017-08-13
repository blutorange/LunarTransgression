package com.github.blutorange.translune.socket;

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

@ServerEndpoint(value="/ws/translune", encoders = LunarEncoder.class, decoders = LunarDecoder.class)
public class LunarEndpoint {
	@Inject @Classed(LunarEndpoint.class) Logger logger;

	public LunarEndpoint() {
		InjectionUtil.inject(this);
	}

	@OnOpen
	public void open(final Session session) {
	}

	@OnMessage
	public void message(final Session session, final String message) {
	}

	@OnClose
	public void close(final Session session, final CloseReason closeReason) {
	}

	@OnError
	public void error(final Session session, final Throwable throwable) {
		logger.warn("error occured during socket communication: " + session.getId(),throwable);
	}
}
