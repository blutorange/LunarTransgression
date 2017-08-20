package com.github.blutorange.translune.socket;

import javax.inject.Inject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.jsoniter.JsonIterator;

public class LunarDecoder implements Decoder.Text<LunarMessage> {
	@Inject @Classed(LunarDecoder.class) Logger logger;

	@Override
	public void destroy() {
		// no state
	}

	@Override
	public void init(final EndpointConfig endpointConfig) {
		ComponentFactory.getSocketComponent().inject(this);
	}

	@Override
	public LunarMessage decode(final String message) throws DecodeException {
		LunarMessage msg;
		try {
			msg = JsonIterator.deserialize(message, LunarMessage.class);
		}
		catch (final Exception e) {
			logger.error("failed to decode message", e);
			throw new DecodeException(message, "failed to decode message", e);
		}

		return msg;
	}

	@Override
	public boolean willDecode(@Nullable final String message) {
		return message != null;
	}
}
