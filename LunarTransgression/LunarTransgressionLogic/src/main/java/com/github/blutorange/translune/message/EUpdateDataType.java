package com.github.blutorange.translune.message;

import javax.websocket.Session;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.ModifiableCharacterState;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.jsoniter.any.Any;

public enum EUpdateDataType {
	NONE {
		@Nullable
		@Override
		public Object update(final Session session, final String details, final ISocketProcessing socketProcessing,
				final ILunarDatabaseManager databaseManager) {
			return null;
		}
	},
	CHARACTER_NICKNAME {
		@Nullable
		@Override
		public Object update(final Session session, final String details, final ISocketProcessing socketProcessing,
				final ILunarDatabaseManager databaseManager) {
			final Player player = databaseManager.find(Player.class, socketProcessing.getNickname(session));
			if (player == null)
				return null;
			final Any any = ComponentFactory.getLunarComponent().jsoniter().get().deserialize(details);
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
	},;

	@Nullable
	public abstract Object update(final Session session, String details, final ISocketProcessing socketProcessing,
			final ILunarDatabaseManager databaseManager) throws Exception;
}