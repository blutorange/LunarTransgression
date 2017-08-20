package com.github.blutorange.translune.util;

public interface IAccessible<T> {
	T get();
	void set(T replacement);
}