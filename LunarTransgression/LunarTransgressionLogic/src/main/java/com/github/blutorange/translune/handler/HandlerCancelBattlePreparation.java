package com.github.blutorange.translune.handler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.EGameState;
import com.github.blutorange.translune.logic.IBattleRunner;
import com.github.blutorange.translune.logic.IBattleStore;
import com.github.blutorange.translune.logic.ISessionStore;
import com.github.blutorange.translune.message.MessageBattlePreparationCancelled;
import com.github.blutorange.translune.message.MessageCancelBattlePreparation;
import com.github.blutorange.translune.message.MessageCancelBattlePreparationResponse;
import com.github.blutorange.translune.message.MessageInviteRejectResponse;
import com.github.blutorange.translune.message.MessageInviteResponse;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.LunarMessage;

@Singleton
public class HandlerCancelBattlePreparation implements ILunarMessageHandler {
	@Inject
	protected ISessionStore sessionStore;
	@Inject
	@Classed(HandlerCancelBattlePreparation.class)
	protected Logger logger;
	@Inject
	protected ISocketProcessing socketProcessing;
	@Inject
	protected IBattleStore battleStore;

	@Inject
	public HandlerCancelBattlePreparation() {
	}

	@SuppressWarnings("resource")
	@Override
	public void handle(final String user, final Session session, final LunarMessage message) {
		if (socketProcessing.getGameState(session) != EGameState.BATTLE_PREPARATION) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageInviteResponse(message, "Game state must be battle preparation for cancelling them."));
			return;
		}

		final MessageCancelBattlePreparation cancel = socketProcessing.getMessage(message, MessageCancelBattlePreparation.class);

		if (cancel == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageInviteRejectResponse(message, "Bad request."));
			return;
		}

		socketProcessing.setGameState(session, EGameState.IN_MENU);

		final ELunarStatusCode code;
		final String resp;

		final IBattleRunner battleRunner = battleStore.retrieve(user);
		if (battleRunner == null) {
			code = ELunarStatusCode.WARN;
			resp = "Battle not prepared anymore.";
		}
		else {
			final String[] players = battleRunner.getPlayers();
			battleStore.removeBattle(players[0], players[1]);
			final Session otherSession = sessionStore.retrieve(players[0].equals(user) ? players[1] : players[0]);
			if (otherSession == null || !otherSession.isOpen()) {
				code = ELunarStatusCode.WARN;
				resp = "Invited user not logged in anymore.";
			}
			else {
				code = ELunarStatusCode.OK;
				resp = "Cancelled";
				if (socketProcessing.getGameState(otherSession) == EGameState.BATTLE_PREPARATION) {
					socketProcessing.setGameState(otherSession, EGameState.IN_MENU);
					socketProcessing.dispatchMessage(otherSession, ELunarStatusCode.OK,
							new MessageBattlePreparationCancelled("Cancel requested by the other player."));
				}
			}
		}

		socketProcessing.dispatchMessage(session, code, new MessageCancelBattlePreparationResponse(message, resp));
	}
}