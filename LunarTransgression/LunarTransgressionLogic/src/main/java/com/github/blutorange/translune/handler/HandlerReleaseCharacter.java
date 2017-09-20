package com.github.blutorange.translune.handler;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

import org.slf4j.Logger;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.EGameState;
import com.github.blutorange.translune.message.MessageInviteResponse;
import com.github.blutorange.translune.message.MessageReleaseCharacter;
import com.github.blutorange.translune.message.MessageReleaseCharacterResponse;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.LunarMessage;
import com.github.blutorange.translune.util.Constants;

@Singleton
public class HandlerReleaseCharacter implements ILunarMessageHandler {
	@Classed(HandlerReleaseCharacter.class)
	@Inject
	protected Logger logger;

	@Inject
	protected ILunarDatabaseManager databaseManager;

	@Inject
	protected ISocketProcessing socketProcessing;

	@Inject
	public HandlerReleaseCharacter() {
	}

	@Override
	public void handle(final String user, final Session session, final LunarMessage message) {
		final EGameState state = socketProcessing.getGameState(session);
		if (state != EGameState.IN_MENU && state != EGameState.BATTLE_LOOT) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageInviteResponse(message, "Game state must be menu or loot for releasing characters"));
			return;
		}

		final MessageReleaseCharacter release = socketProcessing.getMessage(message, MessageReleaseCharacter.class);

		if (release == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageReleaseCharacterResponse(message, "Bad request"));
			return;
		}

		final Player player = databaseManager.find(Player.class, user);
		if (player == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageReleaseCharacterResponse(message, "Player not found."));
			return;
		}

		if (player.getUnmodifiableCharacterStates().size() <= 4) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageReleaseCharacterResponse(message, "Cannot release character, must have at least 4."));
			return;
		}

		final Optional<CharacterState> optionalCharacterState = player.getUnmodifiableCharacterStates().stream()
				.filter(cs -> cs.getPrimaryKey().equals(release.getCharacterState())).findAny();
		if (!optionalCharacterState.isPresent()) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageReleaseCharacterResponse(message, "Character state not found on player."));
			return;
		}

		final CharacterState characterState = optionalCharacterState .get();
		if (characterState.getLevel() < Constants.MIN_RELEASE_LEVEL) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageReleaseCharacterResponse(message, "Cannot release character, release level not reached."));
			return;
		}

		databaseManager.releaseCharacter(characterState);

		socketProcessing.dispatchMessage(session, ELunarStatusCode.OK,
				new MessageReleaseCharacterResponse(message, "Released"));
	}
}