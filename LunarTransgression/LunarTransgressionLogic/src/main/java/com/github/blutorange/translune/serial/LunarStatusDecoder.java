package com.github.blutorange.translune.serial;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.spi.Decoder;

public final class LunarStatusDecoder implements Decoder {
	@Override
	@Nullable
	public ELunarStatusCode decode(final JsonIterator iter) throws IOException {
		if (iter.whatIsNext() == ValueType.NULL) {
			iter.readNull();
			return ELunarStatusCode.OK;
		}
		try {
			return ELunarStatusCode.valueOf(iter.readInt());
		}
		catch (final IllegalArgumentException e) {
			return ELunarStatusCode.OK;
		}
	}
}