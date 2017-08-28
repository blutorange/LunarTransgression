package com.github.blutorange.translune.message;

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
		this(requestMessage.getTime(), message);
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.PREPARE_BATTLE_RESPONSE;
	}
}