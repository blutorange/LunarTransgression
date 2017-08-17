package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ELunarMessageType;

public class MessagePrepareBattleResponse extends AMessageResponse {
	@Deprecated
	public MessagePrepareBattleResponse() {
	}

	public MessagePrepareBattleResponse(final int origin) {
		super(origin);
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.PREPARE_BATTLE_RESPONSE;
	}
}