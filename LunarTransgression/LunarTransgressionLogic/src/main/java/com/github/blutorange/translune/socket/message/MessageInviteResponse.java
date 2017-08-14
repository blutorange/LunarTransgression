package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ELunarMessageType;

public class MessageInviteResponse extends AMessageMessageResponse {
	@Deprecated
	public MessageInviteResponse() {
	}

	public MessageInviteResponse(final int origin, final String message) {
		super(origin, message);
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.INVITE_RESPONSE;
	}
}
