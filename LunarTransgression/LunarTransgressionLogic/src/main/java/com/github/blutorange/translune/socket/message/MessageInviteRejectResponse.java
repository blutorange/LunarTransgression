package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ELunarMessageType;

public class MessageInviteRejectResponse extends AMessageMessageResponse {
	@Deprecated
	public MessageInviteRejectResponse() {
	}

	public MessageInviteRejectResponse(final int origin, final String message) {
		super(origin, message);
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.INVITE_REJECT_RESPONSE;
	}
}
