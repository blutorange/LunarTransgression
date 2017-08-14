package com.github.blutorange.translune.socket;

import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.logic.EGameState;

public interface ISocketProcessing {
	void dispatchMessage(final Session session, ELunarStatusCode status, final ILunarMessage message);

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
	<T extends ILunarMessage> T getMessage(final String payload, final Class<T> clazz);

	void dispatchMessage(Session session, String message);

	void initSession(Session session);
}