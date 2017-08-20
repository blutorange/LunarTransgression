package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.LunarMessage;

public class MessageLootResponse extends AMessageMessageResponse {
	@Deprecated
	public MessageLootResponse() {
	}

	public MessageLootResponse(final int origin, final String message) {
		super(origin, message);
	}

	public MessageLootResponse(final LunarMessage requestMessage, final String message) {
		this(requestMessage.getId(), message);
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.LOOT_RESPONSE;
	}
}