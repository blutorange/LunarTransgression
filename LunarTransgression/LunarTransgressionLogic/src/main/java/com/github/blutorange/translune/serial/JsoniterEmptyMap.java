package com.github.blutorange.translune.serial;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

public class JsoniterEmptyMap<@Nullable K, @Nullable V> implements Map<K, V> {
	private final Map<K,V> map;

	public JsoniterEmptyMap() {
		this.map = Collections.emptyMap();
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(@Nullable final Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(@Nullable final Object value) {
		return map.containsValue(value);
	}

	@Override
	public @Nullable V get(@Nullable final Object key) {
		return map.get(key);
	}

	@Override
	public V put(final K key, final V value) {
		return map.put(key, value);
	}

	@Override
	public @Nullable V remove(@Nullable final Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(final @Nullable Map<? extends K, ? extends V> m) {
		map.putAll(m);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return map.entrySet();
	}
}