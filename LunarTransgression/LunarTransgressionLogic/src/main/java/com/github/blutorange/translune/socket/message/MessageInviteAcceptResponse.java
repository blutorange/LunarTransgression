package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.LunarMessage;

public class MessageInviteAcceptResponse extends AMessageMessageResponse {
	@Deprecated
	public MessageInviteAcceptResponse() {
	}

	public MessageInviteAcceptResponse(final int origin, final String message) {
		super(origin, message);
	}

	public MessageInviteAcceptResponse(final LunarMessage requestMessage, final String message) {
		this(requestMessage.getId(), message);
	}
	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.INVITE_ACCEPT_RESPONSE;
	}
}
