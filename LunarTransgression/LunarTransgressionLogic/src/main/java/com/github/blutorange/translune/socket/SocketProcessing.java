package com.github.blutorange.translune.socket;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.jsoniter.output.JsonStream;

public class SocketProcessing implements ISocketProcessing {

	private final Logger logger;

	public SocketProcessing(@Classed(SocketProcessing.class) final Logger logger) {
		this.logger = logger;
	}

	@Override
	public void dispatchMessage(final Session session, final ILunarMessage message) {
		final String payload = JsonStream.serialize(message);
		// TODO id
		final LunarMessage msg = new LunarMessage(0, message.getMessageType(), payload);
		final String string = JsonStream.serialize(msg);
		try {
			session.getBasicRemote().getSendWriter().write(string);
		}
		catch (final IOException e) {
			logger.error("failed to dispatch message", e);
		}
	}

	@Override
	public void close(final Session session, final CloseCodes closeCode, final String closeReason) {
		try {
			session.close(new CloseReason(closeCode, closeReason));
		}
		catch (final IOException e) {
			logger.error("failed to close session " + session.getId(), e);
		}
	}
}