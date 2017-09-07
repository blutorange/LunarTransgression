package com.github.blutorange.translune.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;

public class MessageCancelBattlePreparation implements ILunarPayload {
	public MessageCancelBattlePreparation() {
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.CANCEL_BATTLE_PREPARATION;
	}
}