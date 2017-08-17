package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarMessage;

public class MessageStepBattle implements ILunarMessage {
	public MessageStepBattle() {
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.STEP_BATTLE;
	}
}