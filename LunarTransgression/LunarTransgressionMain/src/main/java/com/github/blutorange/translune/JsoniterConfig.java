package com.github.blutorange.translune;

import com.github.blutorange.translune.socket.LunarMessage;
import com.jsoniter.spi.TypeLiteral;
import com.jsoniter.static_codegen.StaticCodegenConfig;

public class JsoniterConfig implements StaticCodegenConfig {

	@Override
	public void setup() {
//        JsoniterSpi.registerPropertyDecoder(LunarMessage.class, "score", new Decoder.IntDecoder() {
//            @Override
//            public int decodeInt(final JsonIterator iter) throws IOException {
//                return Integer.parseInt(iter.readString());
//            }
//        });
	}

	@Override
	public TypeLiteral<?>[] whatToCodegen() {
		return new TypeLiteral<?>[]{
			TypeLiteral.create(LunarMessage.class),
		};
	}

}
