package com.github.blutorange.translune.socket;

import javax.websocket.Session;

import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.socket.ILunarMessageHandler.ELunarMessageHandler;

public enum ELunarMessageType {
	AUTHORIZE(ComponentFactory.getLunarComponent().ihandlerAuthorize()),
	AUTHORIZE_RESPONSE(ELunarMessageHandler.NOOP),

	FETCH_DATA(ComponentFactory.getLunarComponent().ihandlerFetchData()),
	FETCH_DATA_RESPONSE(ELunarMessageHandler.NOOP),

	UPDATE_DATA(ComponentFactory.getLunarComponent().ihandlerUpdateData()),
	UPDATE_DATA_RESPONSE(ELunarMessageHandler.NOOP),

	INVITE(ComponentFactory.getLunarComponent().ihandlerInvite()),
	INVITE_RESPONSE(ELunarMessageHandler.NOOP),
	INVITED(ELunarMessageHandler.NOOP),

	INVITE_ACCEPT(ComponentFactory.getLunarComponent().ihandlerInviteAccept()),
	INVITE_ACCEPT_RESPONSE(ELunarMessageHandler.NOOP),
	INVITE_ACCEPTED(ELunarMessageHandler.NOOP),

	INVITE_RETRACT(ComponentFactory.getLunarComponent().ihandlerInviteRetract()),
	INVITE_RETRACT_RESPONSE(ELunarMessageHandler.NOOP),
	INVITE_RETRACTED(ELunarMessageHandler.NOOP),

	INVITE_REJECT(ComponentFactory.getLunarComponent().ihandlerInviteReject()),
	INVITE_REJECT_RESPONSE(ELunarMessageHandler.NOOP),
	INVITE_REJECTED(ELunarMessageHandler.NOOP),

	PREPARE_BATTLE(ComponentFactory.getLunarComponent().ihandlerPrepareBattle()),
	PREPARE_BATTLE_RESPONSE(ELunarMessageHandler.NOOP),

	STEP_BATTLE(ComponentFactory.getLunarComponent().ihandlerStepBattle()),
	STEP_BATTLE_RESPONSE(ELunarMessageHandler.NOOP),

	BATTLE_PREPARED(ELunarMessageHandler.NOOP),
	BATTLE_STEPPED(ELunarMessageHandler.NOOP),
	BATTLE_CANCELLED(ELunarMessageHandler.NOOP),
	BATTLE_ENDED(ELunarMessageHandler.NOOP),

	LOOT(ComponentFactory.getLunarComponent().ihandlerLoot()),
	LOOT_RESPONSE(ELunarMessageHandler.NOOP),

	UNKNOWN(ELunarMessageHandler.NOOP),
	;

	ILunarMessageHandler handler;
	ISocketProcessing socketProcessing;

	private ELunarMessageType(final ILunarMessageHandler handler) {
		this.handler = handler;
		this.socketProcessing = ComponentFactory.getLunarComponent().iSocketProcessing();
	}

	public void handle(final Session session, final LunarMessage message) {
		final String user = socketProcessing.getNickname(session);
		handler.handle(user, session, message);
	}
}