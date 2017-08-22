package com.github.blutorange.translune.logic;

import java.util.List;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;
import com.github.blutorange.translune.util.Constants;

public class ItemExecutorHeal extends AItemExecutor {
	@Override
	public void execute(final Item item, final IBattleContext context, final BattleCommand battleCommand,
			final List<BattleAction> battleActionsMe, final List<BattleAction> battleActionsHim, final int player,
			final int character) {
		final String[] targets = battleCommand.getTargets();
		if (targets.length != 1) {
			handleError(context, battleActionsMe, battleActionsHim, item, player, character, "But the target is gone!");
			return;
		}
		if (!context.removeItem(item, player)) {
			handleError(context, battleActionsMe, battleActionsHim, item, player, character,
					"But the item is no longer there!");
			return;
		}

		final String target = targets[0];
		final int[] characterIndex = context.getCharacterIndex(target);
		final int cur = context.getBattleStatus(characterIndex).getHp();

		context.getBattleStatus(characterIndex).setCurrentHp(cur + item.getPower() * Constants.MAX_RELATIVE_HP / cur);
		battleActionsMe.add(new BattleAction(context.getCharacterState(player, character).getId(), target,
				String.format("%s uses %s!", context.getCharacterState(characterIndex).getNickname(), item.getName())));
	}

	private void handleError(final IBattleContext context, final List<BattleAction> battleActionsMe,
			final List<BattleAction> battleActionsHim, final Item item, final int player, final int character,
			final String error) {
		final CharacterState c = context.getCharacterState(player, character);
		final BattleAction action = new BattleAction(c.getId(), c.getId(),
				String.format("%s uses %s!", c.getNickname(), item.getName()), error);
		battleActionsMe.add(action);
		battleActionsHim.add(action);
	}
}