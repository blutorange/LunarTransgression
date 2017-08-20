package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import org.slf4j.Logger;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.SocketProcessing;

import dagger.Module;
import dagger.Provides;

@Module
public class SocketModule {
	@Provides @Singleton
	static ISocketProcessing provideSocketProcessing(@Classed(SocketProcessing.class) final Logger logger) {
		return new SocketProcessing(logger);
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.AUTHORIZE)
	static ILunarMessageHandler provideMessageHandlerAuthorize() {
		return ComponentFactory.getSocketComponent().handlerAuthorize();
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.INVITE)
	static ILunarMessageHandler provideMessageHandlerInvite() {
		return ComponentFactory.getSocketComponent().handlerInvite();
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.INVITE_ACCEPT)
	static ILunarMessageHandler provideMessageHandlerInviteAccept() {
		return ComponentFactory.getSocketComponent().handlerInviteAccept();
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.INVITE_RETRACT)
	static ILunarMessageHandler provideMessageHandlerInviteRetract() {
		return ComponentFactory.getSocketComponent().handlerInviteRetract();
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.INVITE_REJECT)
	static ILunarMessageHandler provideMessageHandlerInviteReject() {
		return ComponentFactory.getSocketComponent().handlerInviteReject();
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.PREPARE_BATTLE)
	static ILunarMessageHandler provideMessageHandlerPrepareBattle() {
		return ComponentFactory.getSocketComponent().handlerPrepareBattle();
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.STEP_BATTLE)
	static ILunarMessageHandler provideMessageHandlerStepBattle() {
		return ComponentFactory.getSocketComponent().handlerStepBattle();
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.LOOT)
	static ILunarMessageHandler provideMessageHandlerLoot() {
		return ComponentFactory.getSocketComponent().handlerLoot();
	}
}