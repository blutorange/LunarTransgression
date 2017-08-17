package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import org.eclipse.jdt.annotation.NonNull;

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

	@NonNull @LunarMessageTyped(ELunarMessageType.AUTHORIZE) ILunarMessageHandler handlerAuthorize();
	@NonNull @LunarMessageTyped(ELunarMessageType.INVITE) ILunarMessageHandler handlerInvite();
	@NonNull @LunarMessageTyped(ELunarMessageType.INVITE_ACCEPT) ILunarMessageHandler handlerInviteAccept();
	@NonNull @LunarMessageTyped(ELunarMessageType.INVITE_RETRACT) ILunarMessageHandler handlerInviteRetract();
	@NonNull @LunarMessageTyped(ELunarMessageType.INVITE_REJECT) ILunarMessageHandler handlerInviteReject();
	@NonNull @LunarMessageTyped(ELunarMessageType.PREPARE_BATTLE) ILunarMessageHandler handlerPrepareBattle();
	@NonNull @LunarMessageTyped(ELunarMessageType.STEP_BATTLE) ILunarMessageHandler handlerStepBattle();

	@NonNull ISocketProcessing socketProcessing();
}