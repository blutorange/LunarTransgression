package com.github.blutorange.translune.serial;

import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.annotation.Nullable;

import com.jsoniter.spi.MapKeyDecoder;
import com.jsoniter.spi.Slice;

public class JsoniterEnumMapKeyDecoder<E extends Enum<E>> implements MapKeyDecoder {
	private final Class<E> enumType;
	public JsoniterEnumMapKeyDecoder(final Class<E> enumType) {
		this.enumType = enumType;
	}

	@Nullable
	@Override
	public Object decode(@Nullable final Slice encodedMapKey) {
		if (encodedMapKey == null)
			return null;
		final String type = new String(encodedMapKey.data(), encodedMapKey.head(),
				encodedMapKey.tail() - encodedMapKey.head(), StandardCharsets.UTF_8);
		if (type.isEmpty())
			return null;
		try {
			return Enum.valueOf(enumType, type);
		}
		catch (final IllegalArgumentException e) {
			throw new RuntimeException("invalid enum type " + type, e);
		}
	}
}
