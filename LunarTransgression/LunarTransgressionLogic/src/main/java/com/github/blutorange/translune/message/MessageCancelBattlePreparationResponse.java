package com.github.blutorange.translune.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.LunarMessage;

public class MessageCancelBattlePreparationResponse extends AMessageMessageResponse {
	@Deprecated
	public MessageCancelBattlePreparationResponse() {
	}

	public MessageCancelBattlePreparationResponse(final int origin, final String message) {
		super(origin, message);
	}

	public MessageCancelBattlePreparationResponse(final LunarMessage requestMessage, final String message) {
		this(requestMessage.getTime(), message);
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.CANCEL_BATTLE_PREPARATION_RESPONSE;
	}
}