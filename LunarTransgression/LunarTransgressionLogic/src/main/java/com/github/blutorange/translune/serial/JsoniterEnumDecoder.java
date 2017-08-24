package com.github.blutorange.translune.serial;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import com.jsoniter.JsonIterator;
import com.jsoniter.spi.Decoder;

public class JsoniterEnumDecoder<E extends Enum<E>> implements Decoder {
	private final Class<E> enumType;
	public JsoniterEnumDecoder(final Class<E> enumType) {
		this.enumType = enumType;
	}

	@Nullable
	@Override
	public Enum<E> decode(final JsonIterator iter) throws IOException {
		final String type = iter.readString();
		if (type == null || type.isEmpty()) return null;
		try {
			return Enum.valueOf(enumType, type);
		}
		catch (final IllegalArgumentException e) {
			throw new IOException("invalid enum type " + type, e);
		}
	}
}
