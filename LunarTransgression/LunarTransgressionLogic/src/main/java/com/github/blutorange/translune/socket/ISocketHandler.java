package com.github.blutorange.translune.socket;

import javax.websocket.Session;

public interface ISocketHandler {
	void handle(String user, Session session, String payload);
	
	public static enum ESocketHandler implements ISocketHandler {
		NOOP {
			@Override
			public void handle(String user, Session session, String payload) {
			}
		};

		@Override
		public abstract void handle(String user, Session session, String payload);		
	}
}
