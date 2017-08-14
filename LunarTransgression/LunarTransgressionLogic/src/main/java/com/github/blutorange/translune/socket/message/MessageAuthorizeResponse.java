package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ELunarMessageType;

public class MessageAuthorizeResponse extends AMessageResponse {
	@Deprecated
	public MessageAuthorizeResponse() {
	}

	public MessageAuthorizeResponse(final int origin) {
		super(origin);
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.AUTHORIZE_RESPONSE;
	}
}