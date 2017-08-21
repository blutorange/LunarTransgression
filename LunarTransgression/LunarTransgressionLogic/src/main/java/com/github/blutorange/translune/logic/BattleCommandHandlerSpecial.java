package com.github.blutorange.translune.logic;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;

// TODO implement  specials
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
		throw new RuntimeException("TODO - not yet implemented");
	}

	@Override
	public void execute(@NonNull final List<BattleAction> battleActionsMe,
			final List<BattleAction> battleActionsHim) {
		// TODO Implement special
		throw new RuntimeException("TODO - not yet implemented");
	}

	@Override
	public void postProcess() {
	}
}