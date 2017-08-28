package com.github.blutorange.translune.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.LunarMessage;

public class MessageInviteResponse extends AMessageMessageResponse {
	@Deprecated
	public MessageInviteResponse() {
	}

	public MessageInviteResponse(final int origin, final String message) {
		super(origin, message);
	}

	public MessageInviteResponse(final LunarMessage requestMessage, final String message) {
		this(requestMessage.getTime(), message);
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.INVITE_RESPONSE;
	}
}
