package com.github.blutorange.translune.handler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.EGameState;
import com.github.blutorange.translune.logic.IBattleStore;
import com.github.blutorange.translune.logic.IInvitationStore;
import com.github.blutorange.translune.logic.ISessionStore;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.LunarMessage;
import com.github.blutorange.translune.socket.message.MessageInvite;
import com.github.blutorange.translune.socket.message.MessageInviteAccept;
import com.github.blutorange.translune.socket.message.MessageInviteAcceptResponse;
import com.github.blutorange.translune.socket.message.MessageInviteAccepted;
import com.github.blutorange.translune.socket.message.MessageInviteResponse;

@Singleton
public class HandlerInviteAccept implements ILunarMessageHandler {
	@Inject protected ISessionStore sessionStore;
	@Inject @Classed(HandlerInviteAccept.class) protected Logger logger;
	@Inject protected IInvitationStore invitationStore;
	@Inject protected ISocketProcessing socketProcessing;
	@Inject protected IBattleStore battleStore;

	@Inject
	public HandlerInviteAccept() {}

	@SuppressWarnings("resource")
	@Override
	public void handle(final String user, final Session session, final LunarMessage message) {
		if (socketProcessing.getGameState(session) != EGameState.IN_MENU) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageInviteResponse(message, "Game state must be menu for accepting invitations"));
			return;
		}

		final MessageInviteAccept inviteAccept = socketProcessing.getMessage(message, MessageInviteAccept.class);

		if (inviteAccept == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageInviteAcceptResponse(message, StringUtils.EMPTY, "Bad request."));
			return;
		}

		final Session otherSession = sessionStore.retrieve(inviteAccept.getNickname());
		if (otherSession == null || !otherSession.isOpen()) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageInviteAcceptResponse(message, inviteAccept.getNickname(), "Invited user not logged in anymore."));
			invitationStore.removeAllWith(inviteAccept.getNickname());
			return;
		}

		final MessageInvite invitation = invitationStore.remove(inviteAccept.getNickname(), user);
		if (invitation == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageInviteAcceptResponse(message, inviteAccept.getNickname(), "Invitation does not exist anymore, possibly because it was retracted."));
			return;
		}

		socketProcessing.setGameState(session, EGameState.BATTLE_PREPARATION);
		socketProcessing.setGameState(otherSession, EGameState.BATTLE_PREPARATION);
		battleStore.startBattle(inviteAccept.getNickname(), user);

		socketProcessing.dispatchMessage(session, ELunarStatusCode.OK, new MessageInviteAcceptResponse(message, inviteAccept.getNickname(), "Battle negotiated."));
		socketProcessing.dispatchMessage(otherSession, ELunarStatusCode.OK, new MessageInviteAccepted(inviteAccept.getNickname()));
	}
}