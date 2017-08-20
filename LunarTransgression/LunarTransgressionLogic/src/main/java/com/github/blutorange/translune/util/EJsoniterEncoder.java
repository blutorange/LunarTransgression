package com.github.blutorange.translune.util;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Encoder;

public enum EJsoniterEncoder implements Encoder {
	ENUM_WITH_CLASS {
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
	ENUM_WITHOUT_CLASS {
		@Override
		public void encode(final @Nullable Object obj, final JsonStream stream) throws IOException {
			if (obj == null)
				stream.writeNull();
			else {
				final Enum<?> e = (Enum<?>)obj;
				stream.writeVal(e.name());
			}
		}
	},
	THROUGHPASS {
		@Override
		public void encode(@Nullable final Object obj, final JsonStream stream) throws IOException {
			stream.writeVal(obj);
		}
	}
	;

	@Override
	public abstract void encode(@Nullable Object obj, JsonStream stream) throws IOException;
}
