package com.github.blutorange.translune.serial;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Encoder;

public final class LunarStatusEncoder implements Encoder {
	@Override
	public void encode(@Nullable final Object obj, @NonNull final JsonStream stream) throws IOException {
		final ELunarStatusCode status = obj == null ? ELunarStatusCode.OK : (ELunarStatusCode) obj;
		stream.writeVal(status.numerical);
	}
}