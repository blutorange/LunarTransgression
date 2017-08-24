package com.github.blutorange.translune.logic;

import java.util.Arrays;

import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;

public class ItemExecutorHeal extends AItemExecutor {
	@Override
	public void execute(final Item item, final IBattleContext context, final BattleCommand battleCommand,
			final int player, final int character) {
		final IComputedBattleStatus[] targets = context.getTargetsAlive(item, context, battleCommand, player, character);
		final IComputedBattleStatus user = context.getComputedBattleStatus(player, character);

		if (targets.length == 0) {
			handleError(context, user.getCharacterState(), item, "But the target is gone!");
			return;
		}

		if (!context.removeItem(item, player)) {
			handleError(context, user.getCharacterState(), item, "But the item is no longer there!");
			return;
		}

		final String[] messages = new String[targets.length + 1];
		messages[0] = String.format("%s uses %s!", context.getCharacterState(player, character).getNickname(),
				item.getName());
		for (int targetIndex = 0; targetIndex < targets.length; ++targetIndex) {
			@SuppressWarnings("null")
			@NonNull
			final IComputedBattleStatus target = targets[targetIndex];
			final int healAmount = item.getPower();
			target.modifyHp(healAmount);
			messages[targetIndex + 1] = String.format("%s's HP were restored by %d.",
					target.getCharacterState().getNickname(), Integer.valueOf(healAmount));
		}

		@SuppressWarnings("null")
		final BattleAction action = new BattleAction(context.getCharacterState(player, character).getId(),
				Arrays.stream(targets).map(t -> t.getCharacterState().getNickname()).toArray(String[]::new), messages);
		context.getBattleActions(player).add(action);
		context.getBattleActionsOpponent(player).add(action);
	}

	private void handleError(final IBattleContext context, final CharacterState user, final Item item,
			final String error) {
		super.handleError(context, user, String.format("%s uses %s!", user.getNickname(), item.getName()), error);
	}
}