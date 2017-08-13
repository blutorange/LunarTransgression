package com.github.blutorange.translune.logic;

public interface IInitIdStore {

	String store(String nickname);

	boolean assertAndClear(String nickname, String initId);

}