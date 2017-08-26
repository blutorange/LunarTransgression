package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.socket.BattleCommand;

public class BattleCommandHandlerSpecial extends ABattleCommandHandler {

	public BattleCommandHandlerSpecial(final IBattleContext battleContext, final int player, final int character,
			final BattleCommand battleCommand) {
		super(battleContext, player, character, battleCommand);
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void preProcess() {
		// not needed
	}

	@Override
	public void execute() {
		// TODO [MID] implement  specials
		throw new RuntimeException("TODO - not yet implemented");
	}

	@Override
	public void postProcess() {
		// not needed
	}
}