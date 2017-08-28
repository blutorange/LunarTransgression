package com.github.blutorange.translune.logic;

public interface IGlobalBattleEffector {

	/**
	 * @param battleContext
	 * @return True iff this battle effector is to be removed now.
	 */
	boolean beforeTurn(IBattleContext context);

	/**
	 * @param battleContext
	 * @return True iff this battle effector is to be removed now.
	 */
	boolean afterTurn(IBattleContext context);

	/**
	 * @param battleContext
	 */
	void onAdd(IBattleContext context);

	/**
	 * @param battleContext
	 */
	void onRemove(IBattleContext context);

	boolean allowTurn(IBattleContext context, IComputedBattleStatus characterState);
}
