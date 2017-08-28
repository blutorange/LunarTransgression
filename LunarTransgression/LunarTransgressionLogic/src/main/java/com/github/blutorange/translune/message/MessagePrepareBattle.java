package com.github.blutorange.translune.message;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;
import com.jsoniter.annotation.JsonProperty;

public class MessagePrepareBattle implements ILunarPayload {
	private String[] items = ArrayUtils.EMPTY_STRING_ARRAY;

	private String[] characterStates = ArrayUtils.EMPTY_STRING_ARRAY;

	public MessagePrepareBattle() {
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.PREPARE_BATTLE;
	}

	/**
	 * @return the items
	 */
	@JsonProperty(required = true)
	public String[] getItems() {
		return items;
	}

	/**
	 * @return the characterStates
	 */
	@JsonProperty(required = true)
	public String[] getCharacterStates() {
		return characterStates;
	}

	/**
	 * @param items
	 *            the items to set
	 */
	public void setItems(final String @Nullable[] items) {
		this.items = items != null ? items : ArrayUtils.EMPTY_STRING_ARRAY;
	}

	/**
	 * @param characterStates
	 *            the characterStates to set
	 */
	public void setCharacterStates(final String @Nullable[] characterStates) {
		this.characterStates = characterStates != null ? characterStates : ArrayUtils.EMPTY_STRING_ARRAY;
	}
}