package com.github.blutorange.translune.socket;

import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

public interface ISocketProcessing {
	public void dispatchMessage(final Session session, final ILunarMessage message);

	public void close(final Session session, final CloseCodes closeCode, final String closeReason);
}
