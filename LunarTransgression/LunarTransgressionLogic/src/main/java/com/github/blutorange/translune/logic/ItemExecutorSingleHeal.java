package com.github.blutorange.translune.logic;

import java.util.Arrays;
import java.util.List;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;
import com.github.blutorange.translune.util.Constants;

public class ItemExecutorSingleHeal extends AItemExecutor {
	@Override
	public void execute(final Item item, final IBattleContext context, final BattleCommand battleCommand,
			final List<BattleAction> battleActionsMe, final List<BattleAction> battleActionsHim, final int player,
			final int character) {
		final CharacterState[] targets = getTargetsAlive(item, context, battleCommand, player, character);
		if (targets.length == 0) {
			handleError(context, battleActionsMe, battleActionsHim, item, player, character, "But the target is gone!");
			return;
		}
		if (!context.removeItem(item, player)) {
			handleError(context, battleActionsMe, battleActionsHim, item, player, character,
					"But the item is no longer there!");
			return;
		}

		String[] messages = new String[targets.length + 1];
		messages[0] = String.format("%s uses %s!", context.getCharacterState(player, character).getNickname(),
				item.getName());
		for (int targetIndex = 0; targetIndex < targets.length; ++targetIndex) {
			CharacterState target = targets[targetIndex];
			final int[] characterIndex = context.getCharacterIndex(target);
			final int cur = context.getBattleStatus(characterIndex).getHp();
			int healAmount = item.getPower();
			context.getBattleStatus(characterIndex).setCurrentHp(cur + healAmount * Constants.MAX_RELATIVE_HP / cur);
			messages[targetIndex + 1] = String.format("%s's HP were restored by %d.",
					context.getCharacterState(characterIndex).getNickname(), healAmount);
		}

		BattleAction action = new BattleAction(context.getCharacterState(player, character).getId(),
				Arrays.stream(targets).map(CharacterState::getNickname).toArray(String[]::new), messages);
		battleActionsMe.add(action);
		battleActionsHim.add(action);
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