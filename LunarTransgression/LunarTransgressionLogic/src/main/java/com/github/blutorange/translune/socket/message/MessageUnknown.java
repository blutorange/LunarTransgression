package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;

public class MessageUnknown implements ILunarPayload {
	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.UNKNOWN;
	}
}