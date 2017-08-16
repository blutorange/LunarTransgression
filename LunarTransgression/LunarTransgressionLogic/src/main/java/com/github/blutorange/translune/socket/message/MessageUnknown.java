package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarMessage;

public class MessageUnknown implements ILunarMessage {
	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.UNKNOWN;
	}
}