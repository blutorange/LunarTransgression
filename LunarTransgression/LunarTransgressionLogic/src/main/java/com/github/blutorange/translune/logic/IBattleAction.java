package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.db.CharacterState;

public interface IBattleAction {

	/**
	 * @return Message(s) describing the battle action, eg.
	 *         <code>Chimera uses Slash on Phyrant!</code>.
	 */
	String[] getSentences();

	/**
	 * @return {@link CharacterState#getPrimaryKey()} of the character that
	 *         initiated the battle action.
	 */
	String getUser();

	/**
	 * @return {@link CharacterState#getPrimaryKey()} of the characters affected
	 *         by this action.
	 */
	String[] getTargets();

	/**
	 *
	 * @return 0 iff the battle continues, >0 iff this command makes the player win, <0 iff it makes the player lose.
	 */
	int causesEnd();
}