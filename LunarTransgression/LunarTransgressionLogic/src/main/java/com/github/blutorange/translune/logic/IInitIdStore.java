package com.github.blutorange.translune.logic;

public interface IInitIdStore {

	String store(String nickname);

	boolean assertToken(String nickname, String initId);

	void clear(String nickname);

	void cleanExpired();
}