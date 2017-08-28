package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.socket.BattleAction;

class EffectorFlinch implements IGlobalBattleEffector {
	int player;
	int character;

	public EffectorFlinch(final int player, final int character) {
		this.player = player;
		this.character = character;
	}

	public EffectorFlinch(final int[] characterIndex) {
		this(characterIndex[0], characterIndex[1]);
	}

	@Override
	public boolean beforeTurn(final IBattleContext battleContext) {
		return false;
	}

	@Override
	public boolean afterTurn(final IBattleContext context) {
		return true;
	}

	@Override
	public void onAdd(final IBattleContext battleContext) {
		// nothing to do here
	}

	@Override
	public void onRemove(final IBattleContext battleContext) {
		// nothing to do here
	}

	@Override
	public boolean allowTurn(final IBattleContext context, final IComputedBattleStatus characterState) {
		final CharacterState user = context.getCharacterState(player, character);
		final BattleAction action = new BattleAction.Builder().character(user).targets(user)
				.addSentences(String.format("%s flinched and couldn't move.", user.getNickname())).build();
		context.getBattleActions(0).add(action);
		context.getBattleActions(1).add(action);
		return false;
	}
}