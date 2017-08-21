package com.github.blutorange.translune.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.LunarMessage;

public class MessageStepBattleResponse extends AMessageMessageResponse {
	@Deprecated
	public MessageStepBattleResponse() {
	}

	public MessageStepBattleResponse(final int origin, final String message) {
		super(origin, message);
	}

	public MessageStepBattleResponse(final LunarMessage requestMessage, final String message) {
		this(requestMessage.getId(), message);
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.STEP_BATTLE_RESPONSE;
	}
}