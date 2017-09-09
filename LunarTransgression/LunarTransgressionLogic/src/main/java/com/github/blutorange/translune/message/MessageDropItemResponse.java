package com.github.blutorange.translune.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.LunarMessage;

public class MessageDropItemResponse extends AMessageMessageResponse {

	@Deprecated
	public MessageDropItemResponse() {
	}

	public MessageDropItemResponse(final int origin, final String message) {
		super(origin, message);
	}

	public MessageDropItemResponse(final LunarMessage requestMessage, final String message) {
		this(requestMessage.getTime(), message);
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.DROP_ITEM_RESPONSE;
	}
}