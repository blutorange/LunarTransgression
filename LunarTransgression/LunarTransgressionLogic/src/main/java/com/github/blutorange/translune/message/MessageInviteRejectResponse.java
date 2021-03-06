package com.github.blutorange.translune.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.LunarMessage;

public class MessageInviteRejectResponse extends AMessageMessageResponse {
	@Deprecated
	public MessageInviteRejectResponse() {
	}

	public MessageInviteRejectResponse(final int origin, final String message) {
		super(origin, message);
	}

	public MessageInviteRejectResponse(final LunarMessage requestMessage, final String message) {
		this(requestMessage.getTime(), message);
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.INVITE_REJECT_RESPONSE;
	}
}
