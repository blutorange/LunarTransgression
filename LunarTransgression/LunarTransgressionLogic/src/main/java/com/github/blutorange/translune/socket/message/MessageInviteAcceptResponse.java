package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ELunarMessageType;

public class MessageInviteAcceptResponse extends AMessageMessageResponse {
	@Deprecated
	public MessageInviteAcceptResponse() {
	}

	public MessageInviteAcceptResponse(final int origin, final String message) {
		super(origin, message);
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.INVITE_ACCEPT_RESPONSE;
	}
}
