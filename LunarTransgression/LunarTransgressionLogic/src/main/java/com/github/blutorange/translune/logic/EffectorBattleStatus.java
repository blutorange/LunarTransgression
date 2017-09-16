package com.github.blutorange.translune.logic;

import java.util.function.Consumer;

class EffectorBattleStatus extends EffectorSingleCharacter {
	private final Consumer<BattleStatus> consumerAdd;
	private final Consumer<BattleStatus> consumerRemove;

	public EffectorBattleStatus(final Consumer<BattleStatus> consumerAdd, final int player, final int character,
			final Consumer<BattleStatus> consumerRemove) {
		super(player, character);
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
		consumerAdd.accept(battleContext.getBattleStatus(getPlayer(), getCharacter()));
	}

	@Override
	public void onRemove(final IBattleContext battleContext) {
		consumerRemove.accept(battleContext.getBattleStatus(getPlayer(), getCharacter()));
	}

	@Override
	public boolean allowCharacterTurn(final IBattleContext context, final IComputedBattleStatus characterState) {
		return true;
	}
}