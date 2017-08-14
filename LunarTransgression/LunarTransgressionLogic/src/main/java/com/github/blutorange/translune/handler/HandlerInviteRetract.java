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
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.message.MessageInvite;
import com.github.blutorange.translune.socket.message.MessageInviteResponse;
import com.github.blutorange.translune.socket.message.MessageInviteRetract;
import com.github.blutorange.translune.socket.message.MessageInviteRetractResponse;
import com.github.blutorange.translune.socket.message.MessageInviteRetracted;

@Singleton
public class HandlerInviteRetract implements ILunarMessageHandler {
	@Inject
	@Classed(HandlerInviteRetract.class)
	protected Logger logger;
	@Inject
	protected ISessionStore sessionStore;
	@Inject
	protected IInvitationStore invitationStore;
	@Inject
	protected ISocketProcessing socketProcessing;
	@Inject
	protected IBattleStore battleStore;

	@Inject
	public HandlerInviteRetract() {
	}

	@SuppressWarnings("resource")
	@Override
	public void handle(final String user, final Session session, final String payload) {
		if (socketProcessing.getGameState(session) != EGameState.WAITING_FOR_INVITATION_RESPONSE) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR, new MessageInviteResponse(
					"Game state must be waiting for invitation response for retracting invitations."));
			return;
		}

		final MessageInviteRetract inviteRetract = socketProcessing.getMessage(payload, MessageInviteRetract.class);

		if (inviteRetract == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageInviteRetractResponse("Bad request."));
			return;
		}

		final Session otherSession = sessionStore.retrieve(inviteRetract.getNickname());
		if (otherSession == null || !otherSession.isOpen()) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.OK,
					new MessageInviteRetractResponse("Invited user not logged in anymore."));
			invitationStore.removeAllWith(inviteRetract.getNickname());
			return;
		}

		final MessageInvite invitation = invitationStore.remove(inviteRetract.getNickname(), user);
		if (invitation == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.OK, new MessageInviteRetractResponse(
					"Invitation does not exist anymore, possibly because it was retracted."));
			return;
		}

		informOtherUser(otherSession, invitation);
	}

	private void informOtherUser(final Session session, final MessageInvite invitation) {
		socketProcessing.dispatchMessage(session, ELunarStatusCode.OK,
				new MessageInviteRetracted(invitation.getNickname()));
	}
}