package com.github.blutorange.translune.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.LunarMessage;

public class MessageReleaseCharacterResponse extends AMessageMessageResponse {

	@Deprecated
	public MessageReleaseCharacterResponse() {
	}

	public MessageReleaseCharacterResponse(final int origin, final String message) {
		super(origin, message);
	}

	public MessageReleaseCharacterResponse(final LunarMessage requestMessage, final String message) {
		this(requestMessage.getTime(), message);
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.RELEASE_CHARACTER_RESPONSE;
	}
}