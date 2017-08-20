package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.translune.handler.HandlerAuthorize;
import com.github.blutorange.translune.handler.HandlerInvite;
import com.github.blutorange.translune.handler.HandlerInviteAccept;
import com.github.blutorange.translune.handler.HandlerInviteReject;
import com.github.blutorange.translune.handler.HandlerInviteRetract;
import com.github.blutorange.translune.handler.HandlerLoot;
import com.github.blutorange.translune.handler.HandlerPrepareBattle;
import com.github.blutorange.translune.handler.HandlerStepBattle;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.LunarDecoder;
import com.github.blutorange.translune.socket.LunarEncoder;
import com.github.blutorange.translune.socket.LunarEndpoint;

import dagger.Component;

@Singleton
@Component(modules={LoggingModule.class, SocketModule.class, StorageModule.class, DatabaseModule.class})
public interface SocketComponent {
	void inject(LunarEndpoint injectable);
	void inject(LunarDecoder injectable);
	void inject(LunarEncoder sessionBean);

	@NonNull HandlerAuthorize handlerAuthorize();
	@NonNull HandlerInvite handlerInvite();
	@NonNull HandlerInviteAccept handlerInviteAccept();
	@NonNull HandlerInviteRetract handlerInviteRetract();
	@NonNull HandlerInviteReject handlerInviteReject();
	@NonNull HandlerPrepareBattle handlerPrepareBattle();
	@NonNull HandlerStepBattle handlerStepBattle();
	@NonNull HandlerLoot handlerLoot();

	@NonNull @LunarMessageTyped(ELunarMessageType.AUTHORIZE) ILunarMessageHandler ihandlerAuthorize();
	@NonNull @LunarMessageTyped(ELunarMessageType.INVITE) ILunarMessageHandler ihandlerInvite();
	@NonNull @LunarMessageTyped(ELunarMessageType.INVITE_ACCEPT) ILunarMessageHandler ihandlerInviteAccept();
	@NonNull @LunarMessageTyped(ELunarMessageType.INVITE_RETRACT) ILunarMessageHandler ihandlerInviteRetract();
	@NonNull @LunarMessageTyped(ELunarMessageType.INVITE_REJECT) ILunarMessageHandler ihandlerInviteReject();
	@NonNull @LunarMessageTyped(ELunarMessageType.PREPARE_BATTLE) ILunarMessageHandler ihandlerPrepareBattle();
	@NonNull @LunarMessageTyped(ELunarMessageType.STEP_BATTLE) ILunarMessageHandler ihandlerStepBattle();
	@NonNull @LunarMessageTyped(ELunarMessageType.LOOT) ILunarMessageHandler ihandlerLoot();

	@NonNull ISocketProcessing socketProcessing();
}