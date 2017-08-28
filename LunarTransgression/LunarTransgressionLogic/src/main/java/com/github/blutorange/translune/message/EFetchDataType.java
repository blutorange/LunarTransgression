package com.github.blutorange.translune.message;

import javax.websocket.Session;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.socket.ISocketProcessing;

public enum EFetchDataType {
	NONE {
		@Nullable
		@Override
		public Object fetch(final Session session, final ISocketProcessing socketProcessing,
				final ILunarDatabaseManager databaseManager) {
			return null;
		}
	},
	USER_PLAYER {
		@Nullable
		@Override
		public Object fetch(final Session session, final ISocketProcessing socketProcessing,
				final ILunarDatabaseManager databaseManager) {
			return databaseManager.find(Player.class, socketProcessing.getNickname(session));
		}
	};

	@Nullable
	public abstract Object fetch(final Session session, final ISocketProcessing socketProcessing,
			final ILunarDatabaseManager databaseManager);
}