package com.github.blutorange.translune.socket.handler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.IInvitationStore;
import com.github.blutorange.translune.logic.ISessionStore;
import com.github.blutorange.translune.socket.ISocketHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.message.EMessageSeverity;
import com.github.blutorange.translune.socket.message.MessageInvite;
import com.github.blutorange.translune.socket.message.MessageMessage;
import com.jsoniter.JsonIterator;

@Singleton
public class HandlerInvite implements ISocketHandler {
	private final ISessionStore sessionStore;
	private final Logger logger;
	private final IInvitationStore invitationStore;
	private final ISocketProcessing socketProcessing;

	@Inject
	public HandlerInvite(@Classed(HandlerInvite.class) final Logger logger, final IInvitationStore invitationStore,
			final ISessionStore sessionStore, final ISocketProcessing socketProcessing) {
		this.invitationStore = invitationStore;
		this.sessionStore = sessionStore;
		this.logger = logger;
		this.socketProcessing = socketProcessing;
	}

	@Override
	public void handle(final String user, final Session session, final String payload) {
		MessageInvite invitation = null;
		try {
			invitation = JsonIterator.deserialize(payload, MessageInvite.class);
		}
		catch (final Exception e) {
			logger.error("failed to parse message invite", e);
		}
		if (invitation == null)
			return;
		if (!sessionStore.contains(invitation.getNickname())) {
			socketProcessing.dispatchMessage(session,
					new MessageMessage(EMessageSeverity.WARN, "Cannot invite, user not logged in"));
			return;
		}
		invitationStore.add(user, invitation);
	}
}