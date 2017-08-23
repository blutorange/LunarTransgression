package com.github.blutorange.translune.logic;

import java.util.Arrays;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.socket.BattleCommand;

public abstract class AExecutor {
	public CharacterState[] getTargetsAlive(ITargettable targettable, IBattleContext context, BattleCommand battleCommand, int player,
			int character) {
		final CharacterState[] targets = targettable.getTarget().getTargets(context, battleCommand, player, character);
		return Arrays.stream(targets).filter(
				target -> context.getComputedBattleStatus(context.getCharacterIndex(target)).getComputedHp() > 0)
				.toArray(CharacterState[]::new);
	}
}
