package com.github.blutorange.translune.serial;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Encoder;

public final class EmptyMapEncoder implements Encoder {
	@Override
	public void encode(@Nullable final Object obj, @NonNull final JsonStream stream) throws IOException {
		if (obj == null)
			stream.writeNull();
		else
			stream.writeEmptyObject();
	}
}