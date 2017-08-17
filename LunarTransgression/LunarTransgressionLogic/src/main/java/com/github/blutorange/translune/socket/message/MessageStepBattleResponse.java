package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ELunarMessageType;

public class MessageStepBattleResponse extends AMessageResponse {
	@Deprecated
	public MessageStepBattleResponse() {
	}

	public MessageStepBattleResponse(final int origin) {
		super(origin);
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.STEP_BATTLE_RESPONSE;
	}
}