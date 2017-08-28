package com.github.blutorange.translune.logic;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.github.blutorange.translune.util.CustomProperties;

@Singleton
public class InitIdStore implements IInitIdStore {
	private final Map<String, InitIdStoreEntry> store;

	@Inject @Named("secure")
	IRandomSupplier random;

	@Inject
	CustomProperties customProperties;

	@Inject
	public InitIdStore() {
		store = Collections.synchronizedMap(new HashMap<>());
	}

	@Override
	public String store(final String nickname) {
		final byte[] bytes = new byte[32];
		random.get().nextBytes(bytes);
		final String initId = Base64.getEncoder().encodeToString(bytes);
		store.put(nickname, new InitIdStoreEntry(initId));
		return initId;
	}


	@Override
	public boolean assertToken(final String nickname, final String initId) {
		final InitIdStoreEntry entry = store.get(nickname);
		if (entry == null)
			return false;
		if (System.currentTimeMillis() - entry.created > customProperties.getTimeoutInitIdMillis()) {
			store.remove(nickname);
			return false;
		}
		return entry.initId.equals(initId);
	}

	protected static class InitIdStoreEntry {
		public final String initId;
		public final long created;
		protected InitIdStoreEntry(final String initId) {
			this.initId = initId;
			this.created = System.currentTimeMillis();
		}
	}

	@Override
	public void clear(final String nickname) {
		store.remove(nickname);
	}
}