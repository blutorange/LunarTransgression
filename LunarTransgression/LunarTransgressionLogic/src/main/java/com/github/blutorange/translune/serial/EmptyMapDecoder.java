package com.github.blutorange.translune.serial;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.spi.Decoder;

public final class EmptyMapDecoder implements Decoder {
	@Override
	public @Nullable Object decode(@NonNull final JsonIterator iter) throws IOException {
		if (iter.whatIsNext() == ValueType.NULL) {
			iter.readNull();
			return null;
		}
		iter.readObject();
		iter.readObject();
		return JsoniterCollections.EMPTY_MAP;
	}
}