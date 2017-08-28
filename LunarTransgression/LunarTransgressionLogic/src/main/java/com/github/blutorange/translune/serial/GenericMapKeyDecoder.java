package com.github.blutorange.translune.serial;

import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.serial.IJsoniter.IJsoniterSupplier;
import com.jsoniter.spi.MapKeyDecoder;
import com.jsoniter.spi.Slice;

public class GenericMapKeyDecoder<T> implements MapKeyDecoder {
	private final Class<T> clazz;
	private final IJsoniterSupplier jsoniter;
	public GenericMapKeyDecoder(final Class<T> clazz) {
		this.clazz = clazz;
		this.jsoniter = ComponentFactory.getLunarComponent().jsoniter();
	}

	@Nullable
	@Override
	public Object decode(@Nullable final Slice encodedMapKey) {
		if (encodedMapKey == null)
			return null;
		final String json = new String(encodedMapKey.data(), encodedMapKey.head(),
				encodedMapKey.tail() - encodedMapKey.head(), StandardCharsets.UTF_8);
		if (json.isEmpty())
			return null;
		return jsoniter.get().deserialize(json, clazz);
	}
}
