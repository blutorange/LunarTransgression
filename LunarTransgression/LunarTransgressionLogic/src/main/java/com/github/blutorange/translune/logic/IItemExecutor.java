package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.socket.BattleCommand;

public interface IItemExecutor {
	void execute(final Item item, final IBattleContext context, final BattleCommand battleCommand, final int player,
			final int character);
}
