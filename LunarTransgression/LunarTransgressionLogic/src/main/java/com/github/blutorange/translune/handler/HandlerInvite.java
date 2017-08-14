package com.github.blutorange.translune.handler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.EGameState;
import com.github.blutorange.translune.logic.IInvitationStore;
import com.github.blutorange.translune.logic.ISessionStore;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.message.MessageInvite;
import com.github.blutorange.translune.socket.message.MessageInviteResponse;
import com.github.blutorange.translune.socket.message.MessageInvited;

@Singleton
public class HandlerInvite implements ILunarMessageHandler {
	@Classed(HandlerInvite.class)
	@Inject
	protected Logger logger;

	@Inject
	protected ISessionStore sessionStore;

	@Inject
	protected IInvitationStore invitationStore;

	@Inject
	protected ISocketProcessing socketProcessing;

	@Inject
	public HandlerInvite() {
	}

	@SuppressWarnings("resource")
	@Override
	public void handle(final String user, final Session session, final String payload) {
		if (socketProcessing.getGameState(session) != EGameState.IN_MENU) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageInviteResponse("Game state must be menu for sending invitations"));
			return;
		}

		final MessageInvite invitation = socketProcessing.getMessage(payload, MessageInvite.class);

		if (invitation == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageInviteResponse("Bad request"));
			return;
		}

		final Session otherSession = sessionStore.retrieve(invitation.getNickname());
		if (otherSession == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageInviteResponse("Invited user not logged in."));
			return;
		}

		if (socketProcessing.getGameState(otherSession) != EGameState.IN_MENU) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageInviteResponse("Invited user not ready for battle."));
			return;
		}

		informOtherUser(otherSession, invitation);

		invitationStore.add(user, invitation);
		socketProcessing.setGameState(session, EGameState.WAITING_FOR_INVITATION_RESPONSE);
		socketProcessing.dispatchMessage(session, ELunarStatusCode.OK,
				new MessageInviteResponse("Invitation successful"));
	}

	private void informOtherUser(final Session session, final MessageInvite invitation) {
		socketProcessing.dispatchMessage(session, ELunarStatusCode.OK,
				new MessageInvited(invitation.getNickname(), invitation.getMessage()));
	}
}