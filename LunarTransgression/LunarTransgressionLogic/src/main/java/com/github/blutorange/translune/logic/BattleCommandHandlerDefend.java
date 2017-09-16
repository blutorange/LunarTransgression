package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;
import com.github.blutorange.translune.util.Constants;

public class BattleCommandHandlerDefend extends ABattleCommandHandler {
	public BattleCommandHandlerDefend(final IBattleContext battleContext, final int player, final int character,
			final BattleCommand battleCommand) {
		super(battleContext, player, character, battleCommand);
	}

	@Override
	public int getPriority() {
		return Constants.BATTLE_PRIORITY_DEFEND;
	}

	@Override
	public void preProcess() {
		// not needed
	}

	@Override
	public void execute() {
		final IComputedBattleStatus user = context.getComputedBattleStatus(player, character);

		context.pushBattleEffector(new EffectorTurnTimed(1,
				new EffectorBattleStatus(bs -> bs.changeStagePhysicalDefense(Constants.BATTLE_DEFEND_STAGE_INCREASE),
						player, character,
						bs -> bs.changeStagePhysicalDefense(-Constants.BATTLE_DEFEND_STAGE_INCREASE))));

		final String message = String.format("%s defends.", user.getCharacterState().getNickname());
		final BattleAction action = new BattleAction.Builder(context).character(user).addSentences(message).build();
		context.getBattleActions(player).add(action);
		context.getBattleActionsOpponent(player).add(action);

	}

	@Override
	public void postProcess() {
		// not needed
	}
}