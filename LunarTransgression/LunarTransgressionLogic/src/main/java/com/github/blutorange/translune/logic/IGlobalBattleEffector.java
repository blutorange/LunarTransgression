package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.db.CharacterState;

public interface IGlobalBattleEffector {

	/**
	 * @param battleContext
	 * @return True iff this battle effector is to be removed now.
	 */
	boolean beforeTurn(IBattleContext battleContext);

	/**
	 * @param battleContext
	 * @return True iff this battle effector is to be removed now.
	 */
	boolean afterTurn(IBattleContext battleContext);

	/**
	 * @param battleContext
	 */
	void onAdd(IBattleContext battleContext);

	/**
	 * @param battleContext
	 */
	void onRemove(IBattleContext battleContext);

	boolean allowTurn(IBattleContext context, CharacterState characterState);
}
