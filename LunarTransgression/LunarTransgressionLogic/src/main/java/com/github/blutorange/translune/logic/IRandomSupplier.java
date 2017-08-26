package com.github.blutorange.translune.logic;

import java.util.Random;

import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.common.Supplier;

public interface IRandomSupplier extends Supplier<@NonNull Random> {
	@Override
	@NonNull
	Random get();
}
