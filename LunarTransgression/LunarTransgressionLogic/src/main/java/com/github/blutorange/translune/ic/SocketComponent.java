package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import com.github.blutorange.translune.handler.HandlerAuthorize;
import com.github.blutorange.translune.handler.HandlerInvite;
import com.github.blutorange.translune.handler.HandlerInviteAccept;
import com.github.blutorange.translune.handler.HandlerInviteReject;
import com.github.blutorange.translune.handler.HandlerInviteRetract;
import com.github.blutorange.translune.logic.ISessionStore;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.LunarDecoder;
import com.github.blutorange.translune.socket.LunarEncoder;
import com.github.blutorange.translune.socket.LunarEndpoint;

import dagger.Component;

@Singleton
@Component(modules={LoggingModule.class, SocketModule.class, StorageModule.class})
public interface SocketComponent {
	void inject(LunarEndpoint injectable);
	void inject(LunarDecoder injectable);
	void inject(LunarEncoder sessionBean);

	void inject(HandlerAuthorize handler);
	void inject(HandlerInvite handler);
	void inject(HandlerInviteAccept handler);
	void inject(HandlerInviteRetract handler);
	void inject(HandlerInviteReject handler);

	@LunarMessageTyped(ELunarMessageType.AUTHORIZE) ILunarMessageHandler handlerAuthorization();
	@LunarMessageTyped(ELunarMessageType.INVITE) ILunarMessageHandler handlerInvite();
	@LunarMessageTyped(ELunarMessageType.INVITE_ACCEPT) ILunarMessageHandler handlerInviteAccept();
	@LunarMessageTyped(ELunarMessageType.INVITE_RETRACT) ILunarMessageHandler handlerInviteRetract();
	@LunarMessageTyped(ELunarMessageType.INVITE_REJECT) ILunarMessageHandler handlerInviteReject();

	ISessionStore sessionStore();
	ISocketProcessing socketProcessing();
}