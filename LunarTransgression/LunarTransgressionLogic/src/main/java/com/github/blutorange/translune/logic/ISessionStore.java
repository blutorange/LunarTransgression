package com.github.blutorange.translune.logic;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.websocket.Session;

import org.eclipse.jdt.annotation.Nullable;

public interface ISessionStore {
	@Nullable Session store(String nickname, Session session);
	/**
	 * Same as {@link #store(String, Session)}, but synchronizes on the returned session.
	 * @param nickname
	 * @param session
	 * @param consumer
	 */
	<T> T store(String nickname, Session session, Function<@Nullable Session, T> consumer);

	default <T> void store(final String nickname, final Session session, final Consumer<@Nullable Session> consumer) {
		store(nickname, session, oldSession -> {
			consumer.accept(oldSession);
			return null;
		});
	}

	PageableResult findNicknames(Pageable pageable);

	@Nullable Session retrieve(String nickname);

	void remove(String nickname);

	void remove(Session session);

	boolean contains(String nickname);
}