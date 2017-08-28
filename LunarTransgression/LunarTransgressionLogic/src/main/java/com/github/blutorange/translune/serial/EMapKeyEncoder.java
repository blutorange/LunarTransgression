package com.github.blutorange.translune.serial;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.serial.IJsoniter.IJsoniterSupplier;
import com.jsoniter.spi.MapKeyEncoder;

public enum EMapKeyEncoder implements MapKeyEncoder {
	ENUM_WITHOUT_CLASS {
		@Override
		public @NonNull String encode(@Nullable final Object mapKey) {
			if (mapKey == null) {
				return "";
			}
			final Enum<?> e = (Enum<?>)mapKey;
			return e.name();
		}
	},
	GENERIC_WITHOUT_CLASS {
		@Override
		public String encode(@Nullable final Object mapKey) {
			if (mapKey == null)
				return "";
			return jsoniter.get().serialize(mapKey);
		}
	};

	private static final IJsoniterSupplier jsoniter = ComponentFactory.getLunarComponent().jsoniter();

	@Override
	public abstract String encode(@Nullable final Object mapKey);
}
