package com.github.blutorange.translune.handler;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

import org.slf4j.Logger;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.db.ModifiablePlayer;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.EGameState;
import com.github.blutorange.translune.message.MessageDropItem;
import com.github.blutorange.translune.message.MessageDropItemResponse;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.LunarMessage;

@Singleton
public class HandlerDropItem implements ILunarMessageHandler {
	@Classed(HandlerDropItem.class)
	@Inject
	protected Logger logger;

	@Inject
	protected ILunarDatabaseManager databaseManager;

	@Inject
	protected ISocketProcessing socketProcessing;

	@Inject
	public HandlerDropItem() {
	}

	@Override
	public void handle(final String user, final Session session, final LunarMessage message) {
		final EGameState state = socketProcessing.getGameState(session);
		if (state != EGameState.IN_MENU && state != EGameState.BATTLE_LOOT) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageDropItemResponse(message, "Game state must be menu or loot for dropping items"));
			return;
		}

		final MessageDropItem drop = socketProcessing.getMessage(message, MessageDropItem.class);

		if (drop == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageDropItemResponse(message, "Bad request"));
			return;
		}

		final Player player = databaseManager.find(Player.class, user);
		if (player == null) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageDropItemResponse(message, "Player not found."));
			return;
		}

		final Optional<Item> optionalItem = player.getUnmodifiableItems().stream()
				.filter(item -> item.getPrimaryKey().equals(drop.getItem())).findAny();
		if (!optionalItem.isPresent()) {
			socketProcessing.dispatchMessage(session, ELunarStatusCode.GENERIC_ERROR,
					new MessageDropItemResponse(message, "Item not found on player."));
			return;
		}

		final Item item = optionalItem .get();
		databaseManager.modify(player, ModifiablePlayer.class, mp -> {
			mp.removeItem(item);
		});

		socketProcessing.dispatchMessage(session, ELunarStatusCode.OK,
				new MessageDropItemResponse(message, "Dropped"));
	}
}