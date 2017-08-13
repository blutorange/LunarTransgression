package com.github.blutorange.translune.logic;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.blutorange.translune.CustomProperties;

@Singleton
public class InitIdStore implements IInitIdStore {
	private final Map<String, InitIdStoreEntry> store;
	private final ThreadLocal<SecureRandom> threadLocalSecureRandom;

	@Inject
	CustomProperties customProperties;

	@Inject
	public InitIdStore() {
		store = Collections.synchronizedMap(new HashMap<>());
		threadLocalSecureRandom = ThreadLocal.withInitial(SecureRandom::new);
	}

	@Override
	public String store(final String nickname) {
		final byte[] bytes = new byte[32];
		threadLocalSecureRandom.get().nextBytes(bytes);
		final String initId = Base64.getEncoder().encodeToString(bytes);
		store.put(nickname, new InitIdStoreEntry(initId));
		return initId;
	}


	@Override
	public boolean assertAndClear(final String nickname, final String initId) {
		final InitIdStoreEntry entry = store.remove(nickname);
		if (entry == null)
			return false;
		if (System.currentTimeMillis() - entry.created > customProperties.getTimeoutInitIdMillis())
			return false;
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
}