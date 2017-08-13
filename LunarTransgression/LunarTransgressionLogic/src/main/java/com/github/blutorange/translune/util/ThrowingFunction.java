package com.github.blutorange.translune.util;

import org.eclipse.jdt.annotation.Nullable;

public interface ThrowingFunction<T, R, E extends @Nullable Throwable> {
	public R apply(T t) throws E;
}