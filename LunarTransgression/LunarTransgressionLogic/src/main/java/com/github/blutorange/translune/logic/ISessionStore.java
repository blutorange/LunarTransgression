package com.github.blutorange.translune.logic;

import javax.websocket.Session;

import org.eclipse.jdt.annotation.Nullable;

public interface ISessionStore {
	@Nullable Session store(String nickname, Session session);
	@Nullable Session retrieve(String nickname);
	void remove(String nickname);
	void remove(Session session);
	boolean contains(String nickname);
}