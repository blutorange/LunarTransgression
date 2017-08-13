package com.github.blutorange.translune.socket.handler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.IBattleStore;
import com.github.blutorange.translune.logic.IInvitationStore;
import com.github.blutorange.translune.logic.ISessionStore;
import com.github.blutorange.translune.socket.ISocketHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.message.EMessageSeverity;
import com.github.blutorange.translune.socket.message.MessageInvite;
import com.github.blutorange.translune.socket.message.MessageInviteAccept;
import com.github.blutorange.translune.socket.message.MessageMessage;
import com.jsoniter.JsonIterator;

@Singleton
public class HandlerInviteAccept implements ISocketHandler {
	private final ISessionStore sessionStore;
	private final Logger logger;
	private final IInvitationStore invitationStore;
	private final ISocketProcessing socketProcessing;
	private final IBattleStore battleStore;

	@Inject
	public HandlerInviteAccept(@Classed(HandlerInviteAccept.class) final Logger logger,
			final IInvitationStore invitationStore, final ISessionStore sessionStore,
			final ISocketProcessing socketProcessing, final IBattleStore battleStore) {
		this.invitationStore = invitationStore;
		this.sessionStore = sessionStore;
		this.logger = logger;
		this.socketProcessing = socketProcessing;
		this.battleStore = battleStore;
	}

	@Override
	public void handle(final String user, final Session session, final String payload) {
		MessageInviteAccept inviteAccept = null;
		try {
			inviteAccept = JsonIterator.deserialize(payload, MessageInviteAccept.class);
		}
		catch (final Exception e) {
			logger.error("failed to parse message invite accept", e);
		}
		if (inviteAccept == null)
			return;
		if (!sessionStore.contains(inviteAccept.getNickname())) {
			socketProcessing.dispatchMessage(session,
					new MessageMessage(EMessageSeverity.WARN, "Cannot accept invite, user not logged in anymore"));
			invitationStore.removeAllWith(inviteAccept.getNickname());
			return;
		}
		final MessageInvite invitation = invitationStore.remove(inviteAccept.getNickname(), user);
		if (invitation == null) {
			socketProcessing.dispatchMessage(session, new MessageMessage(EMessageSeverity.WARN,
					"Cannot accept invite, no such invitation exists anymore"));
		}
		// TODO START BATTLE, send message to user
		battleStore.addNewBattle(inviteAccept.getNickname(), user);
	}
}