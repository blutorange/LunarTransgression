package com.github.blutorange.translune.socket;

import javax.websocket.Session;

import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.socket.ILunarMessageHandler.ELunarMessageHandler;

public enum ELunarMessageType {
	AUTHORIZE(ComponentFactory.getSocketComponent().ihandlerAuthorize()),
	AUTHORIZE_RESPONSE(ELunarMessageHandler.NOOP),

	INVITE(ComponentFactory.getSocketComponent().ihandlerInvite()),
	INVITE_RESPONSE(ELunarMessageHandler.NOOP),
	INVITED(ELunarMessageHandler.NOOP),

	INVITE_ACCEPT(ComponentFactory.getSocketComponent().ihandlerInviteAccept()),
	INVITE_ACCEPT_RESPONSE(ELunarMessageHandler.NOOP),
	INVITE_ACCEPTED(ELunarMessageHandler.NOOP),

	INVITE_RETRACT(ComponentFactory.getSocketComponent().ihandlerInviteRetract()),
	INVITE_RETRACT_RESPONSE(ELunarMessageHandler.NOOP),
	INVITE_RETRACTED(ELunarMessageHandler.NOOP),

	INVITE_REJECT(ComponentFactory.getSocketComponent().ihandlerInviteReject()),
	INVITE_REJECT_RESPONSE(ELunarMessageHandler.NOOP),

	PREPARE_BATTLE(ComponentFactory.getSocketComponent().ihandlerPrepareBattle()),
	PREPARE_BATTLE_RESPONSE(ELunarMessageHandler.NOOP),

	STEP_BATTLE(ComponentFactory.getSocketComponent().ihandlerStepBattle()),
	STEP_BATTLE_RESPONSE(ELunarMessageHandler.NOOP),

	BATTLE_PREPARED(ELunarMessageHandler.NOOP),
	BATTLE_STEPPED(ELunarMessageHandler.NOOP),
	BATTLE_CANCELLED(ELunarMessageHandler.NOOP),
	BATTLE_ENDED(ELunarMessageHandler.NOOP),

	LOOT(ComponentFactory.getSocketComponent().ihandlerLoot()),
	LOOT_RESPONSE(ELunarMessageHandler.NOOP),

	UNKNOWN(ELunarMessageHandler.NOOP),
	;

	ILunarMessageHandler handler;
	ISocketProcessing socketProcessing;

	private ELunarMessageType(final ILunarMessageHandler handler) {
		this.handler = handler;
		this.socketProcessing = ComponentFactory.getSocketComponent().socketProcessing();
	}

	public void handle(final Session session, final LunarMessage message) {
		final String user = socketProcessing.getNickname(session);
		if (user.isEmpty())
			return;
		handler.handle(user, session, message);
	}
}