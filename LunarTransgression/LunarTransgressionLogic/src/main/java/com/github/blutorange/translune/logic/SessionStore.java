package com.github.blutorange.translune.logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	public @Nullable Session retrieve(final String nickname) {
		return map.get(nickname);
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
		return map.containsKey(nickname);
	}
}