package com.github.blutorange.translune.handler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

import org.slf4j.Logger;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.EGameState;
import com.github.blutorange.translune.message.MessageFetchData;
import com.github.blutorange.translune.message.MessageFetchDataResponse;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.LunarMessage;

@Singleton
public class HandlerFetchData implements ILunarMessageHandler {
	@Inject
	ISocketProcessing socketProcessing;

	@Inject
	ILunarDatabaseManager databaseManager;

	@Inject @Classed(HandlerFetchData.class)
	Logger logger;

	@Inject
	public HandlerFetchData() {}

	@Override
	public void handle(final String user, final Session session, final LunarMessage message) {
				socketProcessing.setGameState(session, EGameState.BATTLE_PREPARATION);
		final MessageFetchData fetchData = socketProcessing.getMessage(message, MessageFetchData.class);
		if (fetchData == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageFetchDataResponse(message));
			return;
		}

		final Object data;
		try {
			data = fetchData.getFetch().fetch(session, socketProcessing, databaseManager);
		}
		catch (final Exception e) {
			logger.error("failed to fetch the requested data", e);
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR, new MessageFetchDataResponse(message));
			return;
		}

		if (data == null) {
			logger.error("failed to fetch the requested data");
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR, new MessageFetchDataResponse(message));
		}
		else
			socketProcessing.dispatchMessage(session, ELunarStatusCode.OK, new MessageFetchDataResponse(message, data));
	}
}