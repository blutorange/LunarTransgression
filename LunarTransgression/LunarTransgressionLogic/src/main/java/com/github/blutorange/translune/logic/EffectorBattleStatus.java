package com.github.blutorange.translune.logic;

import java.util.function.Consumer;

class EffectorBattleStatus implements IGlobalBattleEffector {
	int player;
	int character;
	private final Consumer<BattleStatus> consumerAdd;
	private final Consumer<BattleStatus> consumerRemove;

	public EffectorBattleStatus(final Consumer<BattleStatus> consumerAdd, final int player, final int character,
			final Consumer<BattleStatus> consumerRemove) {
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
	public boolean allowTurn(final IBattleContext context, final IComputedBattleStatus characterState) {
		return true;
	}
}