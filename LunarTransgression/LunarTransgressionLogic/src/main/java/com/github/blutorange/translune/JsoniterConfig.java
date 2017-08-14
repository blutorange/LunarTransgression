package com.github.blutorange.translune;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.LunarMessage;
import com.github.blutorange.translune.socket.message.MessageAuthorize;
import com.github.blutorange.translune.socket.message.MessageInvite;
import com.github.blutorange.translune.socket.message.MessageInviteResponse;
import com.github.blutorange.translune.util.EJsoniterEnumEncoder;
import com.github.blutorange.translune.util.JsoniterEnumDecoder;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;
import com.jsoniter.static_codegen.StaticCodegenConfig;

public class JsoniterConfig implements StaticCodegenConfig {
	@Override
	public void setup() {
		JsoniterSpi.registerPropertyEncoder(LunarMessage.class, "type", EJsoniterEnumEncoder.WITHOUT_CLASS);
        JsoniterSpi.registerPropertyDecoder(LunarMessage.class, "type", new JsoniterEnumDecoder<>(ELunarMessageType.class));
	}

	@Override
	public TypeLiteral<?>[] whatToCodegen() {
		return new TypeLiteral<?>[]{
			TypeLiteral.create(LunarMessage.class),
			TypeLiteral.create(MessageAuthorize.class),
			TypeLiteral.create(MessageInvite.class),
			TypeLiteral.create(MessageInviteResponse.class),
		};
	}
}
