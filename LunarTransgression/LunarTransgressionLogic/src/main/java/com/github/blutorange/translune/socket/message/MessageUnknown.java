package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarMessage;

public enum MessageUnknown implements ILunarMessage {
	INSTANCE;

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.UNKNOWN;
	}
}