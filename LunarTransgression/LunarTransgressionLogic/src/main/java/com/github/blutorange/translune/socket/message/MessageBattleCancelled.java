package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ELunarMessageType;

public class MessageBattleCancelled extends AMessageMessage {
	@Deprecated
	public MessageBattleCancelled() {
	}

	public MessageBattleCancelled(final String message) {
		super(message);
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.BATTLE_CANCELLED;
	}
}