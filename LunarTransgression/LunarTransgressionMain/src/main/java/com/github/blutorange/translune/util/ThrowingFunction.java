package com.github.blutorange.translune.util;

public interface ThrowingFunction<T, R, E extends Throwable> {
	public R apply(T t) throws E;
}