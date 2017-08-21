package com.github.blutorange.translune.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;

public class MessageBattlePrepared implements ILunarPayload {
	public MessageBattlePrepared() {
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.BATTLE_PREPARED;
	}
}