package com.github.blutorange.translune.logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@Singleton
public class SessionStore implements ISessionStore {

	final Map<String, Session> map;

	@Inject
	public SessionStore() {
		map = Collections.synchronizedMap(new HashMap<>());
	}

	@Nullable
	@Override
	public Session store(final String nickname, final Session session) {
		return map.put(nickname, session);
	}

	@Override
	public <T> T store(final String nickname, @NonNull final Session session, final Function<@Nullable Session, T> consumer) {
		synchronized(map) {
			final Session oldSession = map.put(nickname, session);
			return consumer.apply(oldSession);
		}
	}

	@Override
	public @Nullable Session retrieve(final String nickname) {
		final Session s = map.get(nickname);
		if (s == null) return null;
		if (s.isOpen()) return s;
		map.remove(nickname);
		return null;
	}

	@Override
	public void remove(@NonNull final String nickname) {
		map.remove(nickname);
	}

	@Override
	public void remove(@NonNull final Session session) {
		final String id = session.getId();
		final Set<Entry<String, Session>> entrySet = map.entrySet();
		synchronized (map) {
			entrySet.removeIf(entry -> id.equals(entry.getValue().getId()));
		}
	}

	@Override
	public boolean contains(final String nickname) {
		return retrieve(nickname) != null;
	}
}