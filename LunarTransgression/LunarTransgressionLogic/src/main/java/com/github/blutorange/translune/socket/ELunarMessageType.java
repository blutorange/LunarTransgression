package com.github.blutorange.translune.socket;

import javax.websocket.Session;

import com.github.blutorange.translune.Constants;
import com.github.blutorange.translune.ic.DaggerSocketComponent;

public enum ELunarMessageType {
	AUTHORIZE(DaggerSocketComponent.create().handlerAuthorization()),
	INVITE(DaggerSocketComponent.create().handlerInvite()),
	INVITE_ACCEPT(DaggerSocketComponent.create().handlerInviteAccept()),
	MESSAGE((user,session,payload) -> {/**/}),
	UNKNOWN((user,session,payload) -> {/**/})
	;

	ISocketHandler handler;

	private ELunarMessageType(final ISocketHandler handler) {
		this.handler = handler;
	}

	public void handle(final Session session, final String payload) {
		final String user = (String)(session.getUserProperties().get(Constants.SESSION_KEY_NICKNAME));
		if (user == null)
			return;
		handler.handle(user, session, payload);
	}
}