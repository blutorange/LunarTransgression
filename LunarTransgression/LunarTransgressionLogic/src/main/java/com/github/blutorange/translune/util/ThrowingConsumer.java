package com.github.blutorange.translune.util;

import org.eclipse.jdt.annotation.NonNull;

public interface ThrowingConsumer<T, @NonNull E extends Throwable> {
	void accept(T t) throws E;
}