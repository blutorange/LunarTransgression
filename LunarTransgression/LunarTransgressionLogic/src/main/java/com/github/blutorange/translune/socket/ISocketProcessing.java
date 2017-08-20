package com.github.blutorange.translune.socket;

import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.logic.EGameState;

public interface ISocketProcessing {
	Future<@Nullable Void> dispatchMessage(final Session session, ELunarStatusCode status, final ILunarPayload message);

	void close(final Session session, final CloseCodes closeCode, final String closeReason);

	void setStartTimeNow(final Session session);
	/**
	 * @param session Session of interest.
	 * @return Start time in milliseconds (UNIX time).
	 */
	long getStartTimeNow(final Session session);

	void setGameState(final Session session, EGameState gameState);
	EGameState getGameState(final Session session);

	void setNickname(final Session session, String nickname);
	String getNickname(final Session session);

	void markAuthorized(final Session session);
	boolean isAuthorized(final Session session);

	void transfer(Session oldSession, Session session);

	@Nullable
	<T extends ILunarPayload> T getMessage(final String payload, final Class<T> clazz);

	@Nullable
	default <T extends ILunarPayload> T getMessage(final LunarMessage message, final Class<T> clazz) {
		return getMessage(message.getPayload(), clazz);
	}

	Future<@Nullable Void> dispatchMessage(Session session, String message);

	void initSession(Session session);

	AtomicInteger getClientTime(final Session session);

	Queue<LunarMessage> getClientMessageQueue(final Session session);
}