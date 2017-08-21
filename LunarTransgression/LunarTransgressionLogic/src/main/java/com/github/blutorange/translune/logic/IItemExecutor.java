package com.github.blutorange.translune.logic;

import java.util.List;

import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;

public interface IItemExecutor {
	void execute(final Item item, final IBattleContext context, final BattleCommand battleCommand,
			final List<BattleAction> battleActionsMe, final List<BattleAction> battleActionsHim, final int player,
			final int character);
}
