package com.github.blutorange.translune.util;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Encoder;

public enum EJsoniterEnumEncoder implements Encoder {
	WITH_CLASS {
		@Override
		public void encode(final @Nullable Object obj, final JsonStream stream) throws IOException {
			if (obj == null) {
				stream.writeNull();
			}
			else {
				final Enum<?> e = (Enum<?>)obj;
				stream.writeArrayStart();
				stream.writeVal(obj.getClass().getEnclosingClass().getCanonicalName());
				stream.writeMore();
				stream.writeVal(e.name());
				stream.writeArrayEnd();
			}
		}
	},
	WITHOUT_CLASS {
		@Override
		public void encode(final @Nullable Object obj, final JsonStream stream) throws IOException {
			if (obj == null)
				stream.writeNull();
			else {
				final Enum<?> e = (Enum<?>)obj;
				stream.writeVal(e.name());
			}
		}
	};

	@Override
	public abstract void encode(@Nullable Object obj, JsonStream stream) throws IOException;
}
