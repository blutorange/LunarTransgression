package com.github.blutorange.translune.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.LunarMessage;

public class MessageAuthorizeResponse extends AMessageResponse {
	@Deprecated
	public MessageAuthorizeResponse() {
	}

	public MessageAuthorizeResponse(final int origin) {
		super(origin);
	}

	public MessageAuthorizeResponse(final LunarMessage requestMessage) {
		this(requestMessage.getTime());
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.AUTHORIZE_RESPONSE;
	}
}