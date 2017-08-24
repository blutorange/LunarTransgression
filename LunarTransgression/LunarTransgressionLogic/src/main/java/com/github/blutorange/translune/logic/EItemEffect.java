package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.socket.BattleCommand;

public enum EItemEffect implements IItemExecutor {
	HEAL(new ItemExecutorHeal());

	private IItemExecutor executor;

	private EItemEffect(final IItemExecutor executor) {
		this.executor = executor;
	}

	@Override
	public void execute(final Item item, final IBattleContext context, final BattleCommand battleCommand,
			final int player, final int character) {
		executor.execute(item, context, battleCommand, player, character);
	}
}