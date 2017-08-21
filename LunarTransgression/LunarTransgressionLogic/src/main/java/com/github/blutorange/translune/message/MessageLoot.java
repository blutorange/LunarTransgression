package com.github.blutorange.translune.message;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;
import com.jsoniter.annotation.JsonProperty;

public class MessageLoot implements ILunarPayload {
	@JsonProperty(required = false)
	@Nullable
	String characterState;

	@JsonProperty(required = false)
	@Nullable
	String dropCharacterState;

	@JsonProperty(required = false)
	@Nullable
	String item;

	@JsonProperty(required = false)
	@Nullable
	String dropItem;

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
	public String getCharacterState() {
		return characterState;
	}

	/**
	 * @return the item. null iff no item is to be looted.
	 */
	@Nullable
	public String getItem() {
		return item;
	}

	/**
	 * @param characterState the characterState to set
	 */
	public void setCharacterState(@Nullable final String characterState) {
		this.characterState = characterState;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(@Nullable final String item) {
		this.item = item;
	}


	/**
	 * @return the dropCharacterState
	 */
	@Nullable
	public String getDropCharacterState() {
		return dropCharacterState;
	}

	/**
	 * @return the dropItem
	 */
	@Nullable
	public String getDropItem() {
		return dropItem;
	}

	/**
	 * @param dropCharacterState the dropCharacterState to set
	 */
	public void setDropCharacterState(final String dropCharacterState) {
		this.dropCharacterState = dropCharacterState;
	}

	/**
	 * @param dropItem the dropItem to set
	 */
	public void setDropItem(final String dropItem) {
		this.dropItem = dropItem;
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.LOOT;
	}
}