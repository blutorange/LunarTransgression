package com.github.blutorange.translune.logic;

public class EffectorTurnTimed implements IGlobalBattleEffector {

	private int numberOfTurns;
	private final boolean beforeTurn;
	private final IGlobalBattleEffector delegate;

	public EffectorTurnTimed(final int numberOfTurns, final IGlobalBattleEffector delegate) {
		this(numberOfTurns, false, delegate);
	}

	public EffectorTurnTimed(final int numberOfTurns, final boolean beforeTurn, final IGlobalBattleEffector delegate) {
		this.numberOfTurns = numberOfTurns;
		this.beforeTurn = beforeTurn;
		this.delegate = delegate;
	}

	@Override
	public boolean beforeTurn(final IBattleContext battleContext) {
		if (delegate.beforeTurn(battleContext))
			return true;
		if (beforeTurn) {
			--numberOfTurns;
			if (numberOfTurns <= 0)
				return true;
		}
		return false;
	}

	@Override
	public boolean afterTurn(final IBattleContext battleContext) {
		if (delegate.afterTurn(battleContext))
			return true;
		if (!beforeTurn) {
			--numberOfTurns;
			if (numberOfTurns <= 0)
				return true;
		}
		return false;
	}

	@Override
	public void onAdd(final IBattleContext battleContext) {
		delegate.onAdd(battleContext);
	}

	@Override
	public void onRemove(final IBattleContext battleContext) {
		delegate.onRemove(battleContext);
	}

	@Override
	public boolean allowTurn(final IBattleContext context, final IComputedBattleStatus characterState) {
		return delegate.allowTurn(context, characterState);
	}
}