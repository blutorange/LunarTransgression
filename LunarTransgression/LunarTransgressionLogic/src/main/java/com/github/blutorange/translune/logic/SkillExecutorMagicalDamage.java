package com.github.blutorange.translune.logic;

import java.util.List;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;
import com.github.blutorange.translune.util.Constants;

public class SkillExecutorMagicalDamage extends ASkillExecutor {

	@Override
	public void execute(final Skill skill, final IBattleContext context, final BattleCommand battleCommand,
			final List<BattleAction> battleActionsMe, final List<BattleAction> battleActionsHim,
			final int player, final int character) {
		// TODO Implement magical damage.
		final CharacterState[] targets = getTargetsAlive(skill, context, battleCommand, player, character);
		if (targets.length != 1) {
			handleError(context, battleActionsMe, battleActionsHim, skill, player, character, "But the target is gone!");
			return;
		}
		if (!context.removeItem(skill, player)) {
			handleError(context, battleActionsMe, battleActionsHim, skill, player, character,
					"But the item is no longer there!");
			return;
		}

		final CharacterState target = targets[0];
		final int[] characterIndex = context.getCharacterIndex(target);	
		final int cur = context.getBattleStatus(characterIndex).getHp();

		context.getBattleStatus(characterIndex).setCurrentHp(cur + skill.getPower() * Constants.MAX_RELATIVE_HP / cur);
		battleActionsMe.add(new BattleAction(context.getCharacterState(player, character).getId(), target.getId(),
				String.format("%s uses %s!", context.getCharacterState(characterIndex).getNickname(), item.getName())));
	}
	
	private void handleError(final IBattleContext context, final List<BattleAction> battleActionsMe,
			final List<BattleAction> battleActionsHim, final Skill skill, final int player, final int character,
			final String error) {
		final CharacterState c = context.getCharacterState(player, character);
		final BattleAction action = new BattleAction(c.getId(), c.getId(),
				String.format("%s casts %s!", c.getNickname(), skill.getName()), error);
		battleActionsMe.add(action);
		battleActionsHim.add(action);
	}

}