package com.github.blutorange.translune.socket;

import javax.websocket.Session;

public interface ISocketHandler {
	void handle(String user, Session session, String payload);
}
