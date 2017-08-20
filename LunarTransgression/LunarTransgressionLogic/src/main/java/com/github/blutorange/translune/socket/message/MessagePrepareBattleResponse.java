package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.LunarMessage;

public class MessagePrepareBattleResponse extends AMessageMessageResponse {
	@Deprecated
	public MessagePrepareBattleResponse() {
	}

	public MessagePrepareBattleResponse(final int origin, final String message) {
		super(origin, message);
	}

	public MessagePrepareBattleResponse(final LunarMessage requestMessage, final String message) {
		this(requestMessage.getId(), message);
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.PREPARE_BATTLE_RESPONSE;
	}
}