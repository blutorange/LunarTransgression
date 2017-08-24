package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.socket.BattleAction;

public abstract class AExecutor {

	public AExecutor() {
	}

	protected void handleError(final IBattleContext context, final CharacterState user, final String... messages) {
		final BattleAction action = new BattleAction(user.getId(), user.getId(), messages);
		final int player = context.getCharacterIndex(user)[0];
		context.getBattleActions(player).add(action);
		context.getBattleActionsOpponent(player).add(action);
	}
}
