package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ELunarMessageType;

public class MessageInviteRetractResponse extends  AMessageMessageResponse {
	@Deprecated
	public MessageInviteRetractResponse() {
	}

	public MessageInviteRetractResponse(final int origin, final String message) {
		super(origin, message);
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.INVITE_RETRACT_RESPONSE;
	}
}
