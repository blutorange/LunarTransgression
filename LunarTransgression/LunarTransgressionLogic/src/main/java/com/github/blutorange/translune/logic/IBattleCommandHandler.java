package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.socket.BattleCommand;

public interface IBattleCommandHandler {
	/**
	 *
	 * @param command
	 * @param characterState
	 * @param computedBattleStatus
	 * @return
	 * @throws IllegalArgumentException
	 *             When the command is not applicable to the character.
	 */
	public static IBattleCommandHandler create(final IBattleContext battleContext, final int player,
			final int character, final BattleCommand command) throws IllegalArgumentException {
		return command.getType().createHandler(battleContext, player, character, command);
	}

	int getPriority();

	int getSpeed();

	String getUser();

	void preProcess();

	void execute();

	void postProcess();

	int getPlayerIndex();

	int getCharacterIndex();

	CharacterState getCharacterState();

	IBattleContext getBattleContext();
}