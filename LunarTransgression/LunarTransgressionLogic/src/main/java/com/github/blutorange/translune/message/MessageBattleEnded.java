package com.github.blutorange.translune.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;
import com.jsoniter.annotation.JsonProperty;

public class MessageBattleEnded implements ILunarPayload {
	@JsonProperty(required = true)
	boolean isVictory;

	@Deprecated
	public MessageBattleEnded() {
	}

	public MessageBattleEnded(final boolean isVictory) {
		this.isVictory = isVictory;
	}

	/**
	 * @return Whether the player won.
	 */
	public boolean getIsVictory() {
		return isVictory;
	}

	/**
	 * @param isVictory Whether the player won.
	 */
	public void setVictory(final boolean isVictory) {
		this.isVictory = isVictory;
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.BATTLE_ENDED;
	}
}