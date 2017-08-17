package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarMessage;

public class MessageBattlePrepared implements ILunarMessage {
	public MessageBattlePrepared() {
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.BATTLE_PREPARED;
	}
}