package com.github.blutorange.translune.socket;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.EGameState;
import com.github.blutorange.translune.util.Constants;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;

public class SocketProcessing implements ISocketProcessing {

	private final Logger logger;

	public SocketProcessing(@Classed(SocketProcessing.class) final Logger logger) {
		this.logger = logger;
	}

	@Override
	public Future<@Nullable Void> dispatchMessage(final Session session, final String message) {
		return session.getAsyncRemote().sendText(message);
	}

	@Override
	public Future<@Nullable Void> dispatchMessage(final Session session, final ELunarStatusCode status, final ILunarMessage message) {
		final String payload = JsonStream.serialize(message);
		final AtomicInteger time = (AtomicInteger) session.getUserProperties().get(Constants.SESSION_KEY_SERVER_TIME);
		if (time == null)
			throw new RuntimeException("initSession not called, sever time is null");
		final LunarMessage msg = new LunarMessage(time.getAndIncrement(), message.getMessageType(), status, payload);
		return session.getAsyncRemote().sendObject(msg);
	}

	@Override
	public void close(final Session session, final CloseCodes closeCode, final String closeReason) {
		try {
			session.close(new CloseReason(closeCode, closeReason));
		}
		catch (final IOException e) {
			logger.error("failed to close session " + session.getId(), e);
		}
	}

	@Override
	public void setStartTimeNow(final Session session) {
		session.getUserProperties().put(Constants.SESSION_KEY_STARTED, Long.valueOf(System.currentTimeMillis()));
	}

	@Override
	public long getStartTimeNow(final Session session) {
		final Object startTime = session.getUserProperties().get(Constants.SESSION_KEY_STARTED);
		return startTime != null ? ((Long) startTime).longValue() : 0;
	}

	@Override
	public void setGameState(final Session session, final EGameState gameState) {
		session.getUserProperties().put(Constants.SESSION_KEY_GAME_STATE, gameState);
	}

	@Override
	public EGameState getGameState(final Session session) {
		final Object gameState = session.getUserProperties().get(Constants.SESSION_KEY_GAME_STATE);
		return gameState != null ? (EGameState) gameState : EGameState.WAITING_FOR_AUTHORIZATION;
	}

	@Override
	public void setNickname(final Session session, final String nickname) {
		session.getUserProperties().put(Constants.SESSION_KEY_NICKNAME, nickname);
	}

	@Override
	public String getNickname(final Session session) {
		final Object nickname = session.getUserProperties().get(Constants.SESSION_KEY_NICKNAME);
		return nickname != null ? (String) nickname : StringUtils.EMPTY;
	}

	@Override
	public void markAuthorized(final Session session) {
		session.getUserProperties().put(Constants.SESSION_KEY_AUTHORIZED, null);
	}

	@Override
	public boolean isAuthorized(final Session session) {
		return session.getUserProperties().containsKey(Constants.SESSION_KEY_AUTHORIZED);
	}

	@Override
	public void transfer(final Session oldSession, final Session session) {
		session.getUserProperties().putAll(oldSession.getUserProperties());
	}

	@Nullable
	@Override
	public <T extends ILunarMessage> T getMessage(final String payload, final Class<T> clazz) {
		try {
			return JsonIterator.deserialize(payload, clazz);
		}
		catch (final Exception e) {
			logger.error("failed to parse message invite", e);
		}
		return null;
	}

	@Override
	public AtomicInteger getClientTime(final Session session) {
		final Object o = session.getUserProperties().get(Constants.SESSION_KEY_CLIENT_TIME);
		if (o == null)
			throw new RuntimeException("initSession was not called. client time is null");
		return (AtomicInteger) o;
	}

	@Override
	public void initSession(final Session session) {
		session.getUserProperties().put(Constants.SESSION_KEY_SERVER_TIME, new AtomicInteger(0));
		session.getUserProperties().put(Constants.SESSION_KEY_CLIENT_TIME, new AtomicInteger(0));
		session.getUserProperties().put(Constants.SESSION_KEY_CLIENT_MESSAGE_QUEUE,
				new PriorityQueue<LunarMessage>(10, (m1, m2) -> Integer.compare(m1.getTime(), m2.getTime())));
		setStartTimeNow(session);
		setGameState(session, EGameState.WAITING_FOR_AUTHORIZATION);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Queue<LunarMessage> getClientMessageQueue(final Session session) {
		final Object o = session.getUserProperties().get(Constants.SESSION_KEY_CLIENT_MESSAGE_QUEUE);
		if (o == null)
			throw new RuntimeException("initSession was not called, message queue is null");
		return (Queue<LunarMessage>) o;
	}
}