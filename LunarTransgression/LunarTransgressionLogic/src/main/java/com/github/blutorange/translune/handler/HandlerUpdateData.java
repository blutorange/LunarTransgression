package com.github.blutorange.translune.handler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

import org.slf4j.Logger;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.EGameState;
import com.github.blutorange.translune.message.MessageUpdateData;
import com.github.blutorange.translune.message.MessageUpdateDataResponse;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.LunarMessage;

@Singleton
public class HandlerUpdateData implements ILunarMessageHandler {
	@Inject
	ISocketProcessing socketProcessing;

	@Inject
	ILunarDatabaseManager databaseManager;

	@Inject @Classed(HandlerUpdateData.class)
	Logger logger;

	@Inject
	public HandlerUpdateData() {}

	@Override
	public void handle(final String user, final Session session, final LunarMessage message) {
				socketProcessing.setGameState(session, EGameState.BATTLE_PREPARATION);
		final MessageUpdateData updateData = socketProcessing.getMessage(message, MessageUpdateData.class);
		if (updateData == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageUpdateDataResponse(message));
			return;
		}

		final Object data;
		try {
			data = updateData.getUpdate().update(session, updateData.getDetails(), socketProcessing, databaseManager);
		}
		catch (final Exception e) {
			logger.error("failed to update the requested data", e);
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR, new MessageUpdateDataResponse(message));
			return;
		}

		if (data == null) {
			logger.error("failed to update the requested data");
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR, new MessageUpdateDataResponse(message));
		}
		else
			socketProcessing.dispatchMessage(session, ELunarStatusCode.OK, new MessageUpdateDataResponse(message, data));
	}
}