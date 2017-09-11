package com.github.blutorange.translune.socket;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.EGameState;
import com.github.blutorange.translune.logic.IInvitationStore;
import com.github.blutorange.translune.logic.ISessionStore;
import com.github.blutorange.translune.message.MessageInviteRetracted;
import com.github.blutorange.translune.serial.IJsoniter.IJsoniterSupplier;
import com.github.blutorange.translune.util.Constants;

@Singleton
public class SocketProcessing implements ISocketProcessing {
	@Inject
	@Classed(SocketProcessing.class)
	Logger logger;

	@Inject
	ILunarDatabaseManager databaseManager;

	@Inject IJsoniterSupplier jsoniter;

	@Inject
	ISessionStore sessionStore;

	@Inject
	IInvitationStore invitationStore;

	@Inject
	public SocketProcessing() {
	}

	@Override
	public Future<@Nullable Void> dispatchMessage(final Session session, final String message) {
		if (logger.isDebugEnabled()) {
			logger.debug("sending message for session " + session.getId());
			logger.debug(message);
		}
		return session.getAsyncRemote().sendText(message);
	}

	@Override
	public Future<@Nullable Void> dispatchMessage(final Session session, final ELunarStatusCode status,
			final ILunarPayload message) {
		final String payload = jsoniter.get().serialize(message);
		final AtomicInteger time = (AtomicInteger) session.getUserProperties().get(Constants.SESSION_KEY_SERVER_TIME);
		if (time == null)
			throw new RuntimeException("initSession not called, sever time is null");
		final LunarMessage msg = new LunarMessage(time.getAndIncrement(), message.messageType(), status, payload);
		if (logger.isDebugEnabled()) {
			logger.debug("sending message for session " + session.getId());
			logger.debug(msg.toString());
			logger.debug(payload);
		}
		return session.getAsyncRemote().sendObject(msg);
	}

	@SuppressWarnings("resource")
	@Override
	public void dispatchMessage(final String nickname, final ELunarStatusCode status, final ILunarPayload message) {
		final Session session = sessionStore.retrieve(nickname);
		if (session != null)
			dispatchMessage(session, status, message);
	}

	@Override
	public void close(final Session session, final CloseCodes closeCode, final String closeReason) {
		logger.debug(String.format("now closing session %s wit code %s: %s", session.getId(),
				Integer.valueOf(closeCode.getCode()), closeReason));
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
		session.getUserProperties().put(Constants.SESSION_KEY_AUTHORIZED, Boolean.TRUE);
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
	public <T extends ILunarPayload> T getMessage(final String payload, final Class<T> clazz) {
		try {
			return jsoniter.get().deserialize(payload, clazz);
		}
		catch (final Exception e) {
			logger.error("failed to parse message " + clazz.getCanonicalName(), e);
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

	@Override
	public void finalizeSession(@NonNull final Session session) {
		final String nickname = getNickname(session);
		for (final String to : invitationStore.removeAllFrom(nickname))
			dispatchMessage(nickname, ELunarStatusCode.OK, new MessageInviteRetracted(to));
		if (!nickname.isEmpty())
			sessionStore.remove(nickname);
		else
			sessionStore.remove(session);
		databaseManager.detach(Player.class, nickname);
		session.getUserProperties().clear();
	}

}