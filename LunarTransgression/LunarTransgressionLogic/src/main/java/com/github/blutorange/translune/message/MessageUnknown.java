package com.github.blutorange.translune.message;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;

public class MessageUnknown implements ILunarPayload {
	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.UNKNOWN;
	}
}