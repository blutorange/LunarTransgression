package com.github.blutorange.translune.logic;

import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.translune.db.CharacterState;

class EffectorBattleStatus implements IGlobalBattleEffector {
	int player;
	int character;
	private final Consumer<@NonNull BattleStatus> consumerAdd;
	private final Consumer<@NonNull BattleStatus> consumerRemove;

	public EffectorBattleStatus(final Consumer<@NonNull BattleStatus> consumerAdd, final int player, final int character,
			final Consumer<@NonNull BattleStatus> consumerRemove) {
		this.player = player;
		this.character = character;
		this.consumerAdd = consumerAdd;
		this.consumerRemove = consumerRemove;
	}

	@Override
	public boolean beforeTurn(final IBattleContext battleContext) {
		return false;
	}

	@Override
	public boolean afterTurn(final IBattleContext battleContext) {
		return false;
	}

	@Override
	public void onAdd(final IBattleContext battleContext) {
		consumerAdd.accept(battleContext.getBattleStatus(player, character));
	}

	@Override
	public void onRemove(final IBattleContext battleContext) {
		consumerRemove.accept(battleContext.getBattleStatus(player, character));
	}

	@Override
	public boolean allowTurn(final IBattleContext context, final CharacterState characterState) {
		return true;
	}
}