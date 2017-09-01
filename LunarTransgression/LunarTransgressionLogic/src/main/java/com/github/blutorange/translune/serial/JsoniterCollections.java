package com.github.blutorange.translune.serial;

import java.util.Map;

public final class JsoniterCollections {
	@SuppressWarnings("rawtypes")
	public static final Map EMPTY_MAP = new JsoniterEmptyMap<>();

	private JsoniterCollections() {
	}

	@SuppressWarnings("cast")
	public static <K, V> Map<K, V> emptyMap() {
		return (Map<K, V>)EMPTY_MAP;
	}
}
