package com.github.blutorange.translune.handler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.EGameState;
import com.github.blutorange.translune.logic.IBattleRunner;
import com.github.blutorange.translune.logic.IBattleStore;
import com.github.blutorange.translune.message.MessagePrepareBattle;
import com.github.blutorange.translune.message.MessagePrepareBattleResponse;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.LunarMessage;


@Singleton
public class HandlerPrepareBattle implements ILunarMessageHandler {
	@Classed(HandlerPrepareBattle.class)
	@Inject
	Logger logger;

	@Inject
	ISocketProcessing socketProcessing;

	@Inject
	IBattleStore battleStore;

	@Inject
	public HandlerPrepareBattle() {}

	@Override
	public void handle(final String user, final Session session, final LunarMessage message) {
		if (socketProcessing.getGameState(session) != EGameState.BATTLE_PREPARATION) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessagePrepareBattleResponse(message, "Game state must be battle preparation for preparing the battle."));
			return;
		}

		final MessagePrepareBattle prepareBattle = socketProcessing.getMessage(message, MessagePrepareBattle.class);
		if (prepareBattle == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessagePrepareBattleResponse(message, "Bad request."));
			return;
		}

		final IBattleRunner battleRunner = battleStore.retrieve(user);
		if (battleRunner == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessagePrepareBattleResponse(message, "No active battle found."));
			return;
		}
		final String[] characterStates = prepareBattle.getCharacterStates();
		final String[] items = prepareBattle.getItems();

		try {
			battleRunner.preparePlayer(user, characterStates, items);
		}
		catch (final Exception e) {
			logger.error("failed to prepare battle for player " + user, e);
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessagePrepareBattleResponse(message, "Battle could not be prepared: " + e.getMessage()));
			return;
		}

		socketProcessing.dispatchMessage(session, ELunarStatusCode.OK,
				new MessagePrepareBattleResponse(message, "Battle preparation accepted."));
	}
}