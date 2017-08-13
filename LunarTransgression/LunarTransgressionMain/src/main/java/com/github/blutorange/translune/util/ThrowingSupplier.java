package com.github.blutorange.translune.util;

public interface ThrowingSupplier<T, E extends Throwable> {
	public T get() throws E;
}