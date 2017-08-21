package com.github.blutorange.translune.handler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.EGameState;
import com.github.blutorange.translune.logic.IBattleRunner;
import com.github.blutorange.translune.logic.IBattleStore;
import com.github.blutorange.translune.message.MessagePrepareBattleResponse;
import com.github.blutorange.translune.message.MessageStepBattle;
import com.github.blutorange.translune.message.MessageStepBattleResponse;
import com.github.blutorange.translune.socket.BattleCommand;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.LunarMessage;

@Singleton
public class HandlerStepBattle implements ILunarMessageHandler {
	@Classed(HandlerStepBattle.class)
	@Inject
	Logger logger;

	@Inject
	ISocketProcessing socketProcessing;

	@Inject
	IBattleStore battleStore;

	@Inject
	public HandlerStepBattle() {}

	@Override
	public void handle(final String user, final Session session, final LunarMessage message) {
		if (socketProcessing.getGameState(session) != EGameState.IN_BATTLE) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessagePrepareBattleResponse(message, "Game state must be in_battle to perform battle actions."));
			return;
		}

		final MessageStepBattle stepBattle = socketProcessing.getMessage(message, MessageStepBattle.class);
		if (stepBattle == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageStepBattleResponse(message, "Bad request."));
			return;
		}

		final IBattleRunner battleRunner = battleStore.retrieve(user);
		if (battleRunner == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageStepBattleResponse(message, "No active battle found."));
			return;
		}

		final BattleCommand battleAction1 = stepBattle.getBattleCommandCharacter1();
		final BattleCommand battleAction2 = stepBattle.getBattleCommandCharacter2();
		final BattleCommand battleAction3 = stepBattle.getBattleCommandCharacter3();
		final BattleCommand battleAction4 = stepBattle.getBattleCommandCharacter4();

		try {
			battleRunner.battlePlayer(user, battleAction1, battleAction2, battleAction3, battleAction4);
		}
		catch (final Exception e) {
			logger.error("failed to step battle for player " + user, e);
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageStepBattleResponse(message, "Battle could not be prepared: " + e.getMessage()));
			return;
		}

		socketProcessing.dispatchMessage(session, ELunarStatusCode.OK,
				new MessageStepBattleResponse(message, "Battle step accepted."));
	}
}