package com.github.blutorange.translune.message;

import com.github.blutorange.translune.socket.ELunarMessageType;

public class MessageBattlePreparationCancelled extends AMessageMessage {
	@Deprecated
	public MessageBattlePreparationCancelled() {
	}

	public MessageBattlePreparationCancelled(final String message) {
		super(message);
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.BATTLE_PREPARATION_CANCELLED;
	}
}