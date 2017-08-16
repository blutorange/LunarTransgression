package com.github.blutorange.translune.socket;

import javax.websocket.Session;

import com.github.blutorange.translune.ic.DaggerSocketComponent;
import com.github.blutorange.translune.socket.ILunarMessageHandler.ELunarMessageHandler;

public enum ELunarMessageType {
	AUTHORIZE(DaggerSocketComponent.create().handlerAuthorization()),
	AUTHORIZE_RESPONSE(ELunarMessageHandler.NOOP),

	INVITE(DaggerSocketComponent.create().handlerInvite()),
	INVITE_RESPONSE(ELunarMessageHandler.NOOP),
	INVITED(ELunarMessageHandler.NOOP),

	INVITE_ACCEPT(DaggerSocketComponent.create().handlerInviteAccept()),
	INVITE_ACCEPT_RESPONSE(ELunarMessageHandler.NOOP),

	INVITE_RETRACT(DaggerSocketComponent.create().handlerInviteRetract()),
	INVITE_RETRACT_RESPONSE(ELunarMessageHandler.NOOP),
	INVITE_RETRACTED(ELunarMessageHandler.NOOP),

	INVITE_REJECT(DaggerSocketComponent.create().handlerInviteReject()),
	INVITE_REJECT_RESPONSE(ELunarMessageHandler.NOOP),

	UNKNOWN(ELunarMessageHandler.NOOP),
	;

	ILunarMessageHandler handler;
	ISocketProcessing socketProcessing;

	private ELunarMessageType(final ILunarMessageHandler handler) {
		this.handler = handler;
		this.socketProcessing = DaggerSocketComponent.create().socketProcessing();
	}

	public void handle(final Session session, final LunarMessage message) {
		final String user = socketProcessing.getNickname(session);
		if (user.isEmpty())
			return;
		handler.handle(user, session, message);
	}
}