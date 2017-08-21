package com.github.blutorange.translune.logic;

import java.util.List;

import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;
import com.github.blutorange.translune.util.Constants;

public class BattleCommandHandlerBasicAttack extends ABattleCommandHandler {
	public BattleCommandHandlerBasicAttack(final IBattleContext battleContext, final int player, final int character,
			final BattleCommand battleCommand) {
		super(battleContext, player, character, battleCommand);
	}

	@Override
	public int getPriority() {
		return Constants.PRIORITY_BASIC_ATTACK;
	}

	@Override
	public void preProcess() {
	}

	@Override
	public void execute(final List<BattleAction> battleActionsMe,
			final List<BattleAction> battleActionsHim) {
		// TODO Implement basic attack
		throw new RuntimeException("TODO - not yet implemented");
	}

	@Override
	public void postProcess() {
	}
}