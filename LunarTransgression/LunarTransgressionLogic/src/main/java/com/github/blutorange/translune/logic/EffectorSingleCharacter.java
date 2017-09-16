package com.github.blutorange.translune.logic;

public abstract class EffectorSingleCharacter implements IGlobalBattleEffector {
	private final int character;
	private final int player;

	public EffectorSingleCharacter(final int player, final int character) {
		this.player = player;
		this.character = character;
	}

	/**
	 * @return the character
	 */
	public int getCharacter() {
		return character;
	}

	/**
	 * @return the player
	 */
	public int getPlayer() {
		return player;
	}

	@Override
	public final boolean allowTurn(final IBattleContext context, final IComputedBattleStatus characterState) {
		final int[] index = context.getCharacterIndex(characterState);
		if (index[0] != player || index[1] != character)
			return true;
		return allowCharacterTurn(context, characterState);
	}

	protected abstract boolean allowCharacterTurn(final IBattleContext context, final IComputedBattleStatus characterState);
}