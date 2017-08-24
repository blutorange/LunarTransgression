package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.socket.BattleCommand;
import com.github.blutorange.translune.util.Constants;

public class BattleCommandHandlerDefend extends ABattleCommandHandler {
	public BattleCommandHandlerDefend(final IBattleContext battleContext, final int player, final int character,
			final BattleCommand battleCommand) {
		super(battleContext, player, character, battleCommand);
	}

	@Override
	public int getPriority() {
		return Constants.PRIORITY_DEFEND;
	}

	@Override
	public void preProcess() {
		//
	}

	@Override
	public void execute() {
		context.pushBattleEffector(new EffectorTurnTimed(1,
				new EffectorBattleStatus(bs -> bs.changeStagePhysicalDefense(Constants.BATTLE_DEFEND_STAGE_INCREASE),
						player, character,
						bs -> bs.changeStagePhysicalDefense(-Constants.BATTLE_DEFEND_STAGE_INCREASE))));
	}

	@Override
	public void postProcess() {
		//
	}
}