package com.github.blutorange.translune.message;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;
import com.jsoniter.annotation.JsonProperty;

public class MessageLoot implements ILunarPayload {
	@Nullable
	String characterState;

	@Nullable
	String dropCharacterState;

	@Nullable
	String dropItem;

	@Nullable
	String item;

	@Deprecated
	public MessageLoot() {
	}

	public MessageLoot(final String characterState, final String item) {
		this.characterState = characterState;
		this.item = item;
	}

	/**
	 * @return the characterState. null if no character is to be looted.
	 */
	@Nullable
	@JsonProperty(required = false)
	public String getCharacterState() {
		return characterState;
	}

	/**
	 * @return the dropCharacterState
	 */
	@JsonProperty(required = false)
	@Nullable
	public String getDropCharacterState() {
		return dropCharacterState;
	}

	/**
	 * @return the dropItem
	 */
	@Nullable
	@JsonProperty(required = false)
	public String getDropItem() {
		return dropItem;
	}

	/**
	 * @return the item. null iff no item is to be looted.
	 */
	@JsonProperty(required = false)
	@Nullable
	public String getItem() {
		return item;
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.LOOT;
	}

	/**
	 * @param characterState
	 *            the characterState to set
	 */
	public void setCharacterState(@Nullable final String characterState) {
		this.characterState = characterState;
	}

	/**
	 * @param dropCharacterState
	 *            the dropCharacterState to set
	 */
	public void setDropCharacterState(final String dropCharacterState) {
		this.dropCharacterState = dropCharacterState;
	}

	/**
	 * @param dropItem
	 *            the dropItem to set
	 */
	public void setDropItem(final String dropItem) {
		this.dropItem = dropItem;
	}

	/**
	 * @param item
	 *            the item to set
	 */
	public void setItem(@Nullable final String item) {
		this.item = item;
	}
}