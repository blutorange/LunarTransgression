package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarMessage;

public class MessagePrepareBattle implements ILunarMessage {
	public MessagePrepareBattle() {
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.PREPARE_BATTLE;
	}
}