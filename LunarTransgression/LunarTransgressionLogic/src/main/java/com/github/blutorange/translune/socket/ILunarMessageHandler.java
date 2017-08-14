package com.github.blutorange.translune.socket;

import javax.websocket.Session;

public interface ILunarMessageHandler {
	void handle(String user, Session session, String payload);

	public static enum ELunarMessageHandler implements ILunarMessageHandler {
		NOOP {
			@Override
			public void handle(final String user, final Session session, final String payload) {
				// noop
			}
		},
		;

		@Override
		public abstract void handle(String user, Session session, String payload);
	}
}
