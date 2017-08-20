package com.github.blutorange.translune.handler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.CharacterStateBuilder;
import com.github.blutorange.translune.db.EEntityMeta;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.db.ModifiablePlayer;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.EGameState;
import com.github.blutorange.translune.logic.IBattleStore;
import com.github.blutorange.translune.logic.ILootable;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.LunarMessage;
import com.github.blutorange.translune.socket.message.MessageLoot;
import com.github.blutorange.translune.socket.message.MessageLootResponse;
import com.github.blutorange.translune.util.Constants;

@Singleton
public class HandlerLoot implements ILunarMessageHandler {
	@Inject
	@Classed(HandlerLoot.class)
	Logger logger;

	@Inject
	ISocketProcessing socketProcessing;

	@Inject
	IBattleStore battleStore;

	@Inject
	ILunarDatabaseManager databaseManager;

	@Inject
	public HandlerLoot() {
	}

	@Override
	public void handle(final String user, final Session session, final LunarMessage message) {
		if (socketProcessing.getGameState(session) != EGameState.BATTLE_LOOT) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageLootResponse(message, "Game state must be battle_loot to get perform looting."));
			return;
		}

		final MessageLoot messageLoot = socketProcessing.getMessage(message, MessageLoot.class);
		if (messageLoot == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageLootResponse(message, "Bad request."));
			return;
		}

		final ILootable lootable = battleStore.getLoot(user);
		if (lootable == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageLootResponse(message, "No lootables found."));
			return;
		}

		final Player player = databaseManager.find(EEntityMeta.PLAYER, user);
		if (player == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageLootResponse(message, "Player does not exist."));
			return;
		}

		doLoot(session, message, player, messageLoot, lootable);

		socketProcessing.dispatchMessage(session, ELunarStatusCode.OK,
				new MessageLootResponse(message, "Battle step accepted."));
	}

	private void doLoot(final Session session, final LunarMessage message, final Player player,
			final MessageLoot messageLoot, final ILootable lootable) {
		final String characterToLoot = messageLoot.getCharacterState();
		final CharacterState addCharacterState;
		if (characterToLoot != null) {
			addCharacterState = findCharacterState(lootable.getCharacterStates(), characterToLoot);
			if (addCharacterState == null) {
				socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
						new MessageLootResponse(message, "Could not find the requested character."));
				return;
			}
		}
		else
			addCharacterState = null;

		final String itemToLoot = messageLoot.getItem();
		final Item addItem;
		if (itemToLoot != null) {
			addItem = findItem(lootable.getCharacterStates(), itemToLoot);
			if (addItem == null) {
				socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
						new MessageLootResponse(message, "Could not find the requested item."));
				return;
			}
		}
		else
			addItem = null;

		final CharacterState dropCharacterState;
		final Item dropItem;

		if (player.getItemsUnmodifiable().size() >= Constants.MAX_ITEMS) {
			final String characterStateToDrop = messageLoot.getDropCharacterState();
			if (characterStateToDrop == null) {
				socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
						new MessageLootResponse(message, "A character must be dropped to loot the character."));
				return;
			}
			dropCharacterState = findDropCharacter(player, characterStateToDrop);
			if (dropCharacterState == null) {
				socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
						new MessageLootResponse(message, "Character to drop does not exist."));
				return;
			}
		}
		else
			dropCharacterState = null;

		if (player.getItemsUnmodifiable().size() >= Constants.MAX_ITEMS) {
			final String itemToDrop = messageLoot.getDropItem();
			if (itemToDrop == null) {
				socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
						new MessageLootResponse(message, "An item must be dropped to loot the item."));
				return;
			}
			dropItem = findDropItem(player, itemToDrop);
			if (dropItem == null) {
				socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
						new MessageLootResponse(message, "Item to drop does not exist."));
				return;
			}
		}
		else
			dropItem = null;

		final CharacterState newCharacterState;
		if (addCharacterState != null)
			newCharacterState = new CharacterStateBuilder().setCharacter(addCharacterState.getCharacter())
				.randomIvs().randomNature().build();
		else
			newCharacterState = null;

		// TODO Implement drop and add
		// Drop dropItem, dropCharacterState if not null
		// Add addItem, addCharacterState if not null
		databaseManager.modify(player, ModifiablePlayer.class, mp -> {
			if (dropItem != null)
				mp.removeItem(dropItem);
			if (addItem != null)
				mp.addItem(addItem);
			if (dropCharacterState != null)
				mp.removeCharacterState(dropCharacterState);
			if (newCharacterState != null)
				mp.addCharacterState(newCharacterState);
		});
		if (dropCharacterState != null)
			databaseManager.delete(dropCharacterState);
		if (newCharacterState != null)
			databaseManager.persist(newCharacterState);
	}

	@Nullable
	private Item findDropItem(final Player player, final String dropItem) {
		for (final Item item : player.getItemsUnmodifiable()) {
			if (item.getPrimaryKey().equals(dropItem))
				return item;
		}
		return null;
	}

	@Nullable
	private CharacterState findDropCharacter(final Player player, final String dropCharacterState) {
		for (final CharacterState characterState : player.getCharacterStatesUnmodifiable()) {
			if (characterState.getPrimaryKey().equals(dropCharacterState))
				return characterState;
		}
		return null;
	}

	@Nullable
	private CharacterState findCharacterState(final String[] characterStates, final String characterState) {
		if (!ArrayUtils.contains(characterStates, characterState))
			return null;
		return databaseManager.<@NonNull CharacterState> find(EEntityMeta.CHARACTER_STATE, characterState);
	}

	@Nullable
	private Item findItem(final String[] items, final String item) {
		if (!ArrayUtils.contains(items, item))
			return null;
		return databaseManager.<@NonNull Item> find(EEntityMeta.ITEM, item);
	}
}