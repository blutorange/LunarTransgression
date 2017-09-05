package com.github.blutorange.translune.logic;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.common.Predicate;
import com.github.blutorange.translune.serial.Filterable;
import com.github.blutorange.translune.socket.ISocketProcessing;

@Singleton
public class SessionStore implements ISessionStore {

	final Map<String, Session> map;

	@Inject ISocketProcessing socketProcessing;

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
	public <T> T store(final String nickname, @NonNull final Session session,
			final Function<@Nullable Session, T> consumer) {
		synchronized (map) {
			final Session oldSession = map.put(nickname, session);
			return consumer.apply(oldSession);
		}
	}

	@Override
	public @Nullable Session retrieve(final String nickname) {
		final Session s = map.get(nickname);
		if (s == null)
			return null;
		if (s.isOpen())
			return s;
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

	@Override
	public PageableResult findNicknames(final Pageable pageable) {
		final Orderable[] orderable = pageable.getOrderBy();
		final int order = orderable.length == 0 || orderable[0].getOrderDirection() == EOrderDirection.ASC ? 1 : -1;
		final Filterable filterable = pageable.getFilter().length == 0 ? null  : pageable.getFilter()[0];
		final Predicate<Entry<String, Session>> filter;
		if (filterable != null && filterable.getLhs().equals("nickname")) {
			final String rhs = filterable.getRhs().toLowerCase(Locale.ROOT);
			switch (filterable.getOperator().toLowerCase(Locale.ROOT)) {
			case "like":
				filter = entry -> entry.getKey().toLowerCase(Locale.ROOT).contains(rhs);
				break;
			case "startswith":
				filter = entry -> entry.getKey().toLowerCase(Locale.ROOT).startsWith(rhs);
				break;
			case "endswith":
				filter = entry -> entry.getKey().toLowerCase(Locale.ROOT).endsWith(rhs);
				break;
			default:
				filter = entry -> true;
			}
		}
		else
			filter = entry -> true;
		final Predicate<Entry<String,Session>> filterInMenu = entry -> socketProcessing.getGameState(entry.getValue()) == EGameState.IN_MENU;
		final Comparator<Entry<String, ?>> comparator = (e1, e2) -> order * e1.getKey().compareTo(e2.getKey());
		final int totalFiltered = map.entrySet()
				.stream()
				.filter(filter.and(filterInMenu))
				.mapToInt(e -> 1)
				.sum();
		final String[] list = map.entrySet().stream()
				.sorted(comparator)
				.filter(filter.and(filterInMenu))
				.map(Entry::getKey)
				.skip(pageable.getOffset())
				.limit(pageable.getCount())
				.toArray(String[]::new);
		return new PageableResult(map.size(), totalFiltered, list);
	}
}