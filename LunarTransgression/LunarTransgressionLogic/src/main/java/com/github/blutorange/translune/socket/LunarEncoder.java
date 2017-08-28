package com.github.blutorange.translune.socket;

import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.serial.IJsoniter.IJsoniterSupplier;

public class LunarEncoder implements Encoder.Text<LunarMessage> {
	@Inject @Classed(LunarEncoder.class) Logger logger;
	@Inject IJsoniterSupplier jsoniter;

	@Override
	public void destroy() {
		// no state
	}

	@Override
	public void init(final EndpointConfig endpointConfig) {
		ComponentFactory.getLunarComponent().inject(this);
	}

	@Override
	public String encode(final LunarMessage message) throws EncodeException {
		try {
			return jsoniter.get().serialize(message);
		}
		catch (final Exception e) {
			logger.error("failed to encode message", e);
			throw new EncodeException(message, "failed to encode message", e);
		}
	}
}
