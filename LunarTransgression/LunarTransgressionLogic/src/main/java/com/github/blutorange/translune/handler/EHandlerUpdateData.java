package com.github.blutorange.translune.handler;

import javax.inject.Inject;
import javax.websocket.Session;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.ModifiableCharacterState;
import com.github.blutorange.translune.db.ModifiablePlayer;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.serial.IJsoniter.IJsoniterSupplier;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.jsoniter.any.Any;

public enum EHandlerUpdateData {
	NONE {
		@Nullable
		@Override
		public Object update(final Session session, final String details) {
			return null;
		}
	},
	CHARACTER_NICKNAME {
		@Nullable
		@Override
		public Object update(final Session session, final String details) {
			final Player player = databaseManager.find(Player.class, socketProcessing.getNickname(session));
			if (player == null)
				return null;
			final Any any = jsoniter.get().deserialize(details);
			final String targetId = any.toString("id");
			final String newNickname = any.toString("nickname");
			if (newNickname == null || targetId == null)
				return null;
			final CharacterState characterState = player.getUnmodifiableCharacterStates().stream()
					.filter(cs -> cs.getId().equals(targetId)).findFirst().orElse(null);
			if (characterState == null)
				return null;
			databaseManager.modify(characterState, ModifiableCharacterState.class, cs -> {
				cs.setNickname(newNickname);
			});
			return characterState.getNickname();
		}
	},
	PLAYER_DESCRIPTION {
		@Override
		public @Nullable Object update(final Session session, final String details) throws Exception {
			final Player player = databaseManager.find(Player.class, socketProcessing.getNickname(session));
			if (player == null)
				return null;
			databaseManager.modify(player, ModifiablePlayer.class, mp -> {
				mp.setDescription(details);
			});
			return player.getDescription();
		}
	}
	;

	@Inject
	ILunarDatabaseManager databaseManager;

	@Inject
	ISocketProcessing socketProcessing;

	@Inject
	IJsoniterSupplier jsoniter;

	private EHandlerUpdateData() {
		ComponentFactory.getLunarComponent().inject(this);
	}

	@Nullable
	public abstract Object update(final Session session, String details) throws Exception;
}