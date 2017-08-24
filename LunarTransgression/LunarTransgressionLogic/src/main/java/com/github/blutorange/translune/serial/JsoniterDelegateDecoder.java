package com.github.blutorange.translune.serial;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.spi.Decoder;

public class JsoniterDelegateDecoder implements Decoder {
	private final Class<?> implementingClass;

	public JsoniterDelegateDecoder(final Class<?> implementingClass) {
		this.implementingClass = implementingClass;
	}

	@Override
	@Nullable
	public Object decode(final JsonIterator iter) throws IOException {
		if (iter.whatIsNext() == ValueType.NULL) {
			iter.readNull();
			return null;
		}
		return iter.read(implementingClass);
	}
}
