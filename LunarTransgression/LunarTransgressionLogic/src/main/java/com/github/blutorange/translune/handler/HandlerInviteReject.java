package com.github.blutorange.translune.handler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.EGameState;
import com.github.blutorange.translune.logic.IBattleStore;
import com.github.blutorange.translune.logic.IInvitationStore;
import com.github.blutorange.translune.logic.ISessionStore;
import com.github.blutorange.translune.message.MessageInvite;
import com.github.blutorange.translune.message.MessageInviteReject;
import com.github.blutorange.translune.message.MessageInviteRejectResponse;
import com.github.blutorange.translune.message.MessageInviteResponse;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.LunarMessage;

@Singleton
public class HandlerInviteReject implements ILunarMessageHandler {
	@Inject
	protected ISessionStore sessionStore;
	@Inject
	@Classed(HandlerInviteReject.class)
	protected Logger logger;
	@Inject
	protected IInvitationStore invitationStore;
	@Inject
	protected ISocketProcessing socketProcessing;
	@Inject
	protected IBattleStore battleStore;

	@Inject
	public HandlerInviteReject() {
	}

	@SuppressWarnings("resource")
	@Override
	public void handle(final String user, final Session session, final LunarMessage message) {
		if (socketProcessing.getGameState(session) != EGameState.IN_MENU) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageInviteResponse(message, "Game state must be menu for rejecting invitations."));
			return;
		}

		final MessageInviteReject inviteReject = socketProcessing.getMessage(message, MessageInviteReject.class);

		if (inviteReject == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageInviteRejectResponse(message, "Bad request."));
			return;
		}

		final Session otherSession = sessionStore.retrieve(inviteReject.getNickname());
		if (otherSession == null || !otherSession.isOpen()) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.OK,
					new MessageInviteRejectResponse(message, "Invited user not logged in anymore."));
			invitationStore.removeAllWith(inviteReject.getNickname());
			return;
		}

		final MessageInvite invitation = invitationStore.remove(inviteReject.getNickname(), user);
		if (invitation == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.OK, new MessageInviteRejectResponse(
					message, "Invitation does not exist anymore, possibly because it was retracted."));
			return;
		}

		socketProcessing.setGameState(otherSession, EGameState.IN_MENU);

		informOtherUser(otherSession, invitation);
	}

	private void informOtherUser(final Session session, final MessageInvite invitation) {
		socketProcessing.dispatchMessage(session, ELunarStatusCode.OK,
				new MessageInviteReject(invitation.getNickname()));
	}
}