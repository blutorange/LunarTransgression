package com.github.blutorange.translune.util;

public interface ThrowingRunnable<E extends Throwable> {
	public void run() throws E;
}