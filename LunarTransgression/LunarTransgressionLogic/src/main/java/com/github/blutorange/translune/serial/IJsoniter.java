package com.github.blutorange.translune.serial;

import com.jsoniter.any.Any;

public interface IJsoniter {
	<T> T deserialize(String input, Class<T> clazz);
	String serialize(Object object);

	public static interface IJsoniterSupplier {
		IJsoniter get();
	}

	Any deserialize(String data);
}