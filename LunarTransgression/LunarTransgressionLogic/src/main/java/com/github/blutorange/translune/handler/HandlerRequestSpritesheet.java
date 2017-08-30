package com.github.blutorange.translune.handler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Scheduler;
import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.IInitIdStore;
import com.github.blutorange.translune.logic.ISessionStore;
import com.github.blutorange.translune.message.MessageRequestSpritesheet;
import com.github.blutorange.translune.message.MessageRequestSpritesheetResponse;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.LunarMessage;

@Singleton
public class HandlerRequestSpritesheet implements ILunarMessageHandler {
	@Inject
	ISocketProcessing socketProcessing;
	
	@Inject
	@Classed(HandlerRequestSpritesheet.class)
	Logger logger;
	
	@Inject
	ISessionStore sessionStore;
	
	@Inject
	public HandlerRequestSpritesheet() {
	}

	@Override
	public void handle(String user, Session session, LunarMessage message) {		
		final MessageRequestSpritesheet requestSpritesheet = socketProcessing.getMessage(message, MessageRequestSpritesheet.class);
		if (requestSpritesheet == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageRequestSpritesheetResponse(message, StringUtils.EMPTY));
			return;
		}
		
		String token = UUID.randomUUID().toString();	
		
		requestSpritesheet.getResourceNames();
		Map<String, Future<String>> map = socketProcessing.getSpritesheetResources(session);
		if (!map.containsKey(token)) {
			map.put(token, value);
		}
		
		socketProcessing.dispatchMessage(session, ELunarStatusCode.OK,
				new MessageRequestSpritesheetResponse(message, token));
	}
}
