package com.github.blutorange.translune.util;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.spi.Decoder;

public enum EJsoniterEnumDecoder implements Decoder {
	WITH_CLASS;

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Nullable
	@Override
	public Enum<?> decode(final JsonIterator iter) throws IOException {
		if (iter.whatIsNext().equals(ValueType.NULL)) {
			iter.readNull();
			return null;
		}
		iter.readArray();
		final String clazz = iter.readString();
		iter.readArray();
		final String value = iter.readString();
		iter.readArray();
		if (clazz == null || value == null) {
			throw new IOException("enum type or value missing: " + clazz + ", " + value);
		}
		try {
			final Class<Enum> enumType = (Class<Enum>)Class.forName(clazz);
			return Enum.valueOf(enumType, value);
		}
		catch (final ClassNotFoundException | IllegalArgumentException e) {
			throw new IOException("invalid enum " + clazz + ", " + value, e);
		}
	}
}
