package com.github.blutorange.translune.logic;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.blutorange.translune.ic.ComponentFactory;

public abstract class AExecutor {
	@Inject
	protected IBattleProcessing battleProcessing;

	@Inject @Named("basic")
	protected IRandomSupplier random;

	public AExecutor() {
		ComponentFactory.getLunarComponent().inject(this);
	}
}