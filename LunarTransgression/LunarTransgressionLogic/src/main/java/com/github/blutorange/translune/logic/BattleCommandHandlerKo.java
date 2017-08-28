package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;
import com.github.blutorange.translune.util.Constants;

public class BattleCommandHandlerKo extends ABattleCommandHandler {

	public BattleCommandHandlerKo(final IBattleContext battleContext, final int player, final int character,
			final BattleCommand battleCommand) {
		super(battleContext, player, character, battleCommand);
	}

	@Override
	public int getPriority() {
		return Constants.MIN_PRIORITY;
	}

	@Override
	public void preProcess() {
		// not needed
	}

	@Override
	public void execute() {
		final BattleAction action = new BattleAction.Builder().character(getCharacterState())
				.addSentences(
						String.format("%s is knocked out cold and cannot move.", getCharacterState().getNickname()))
				.build();
		context.getBattleActions(player).add(action);
		context.getBattleActionsOpponent(player).add(action);
	}

	@Override
	public void postProcess() {
		// not needed
	}
}