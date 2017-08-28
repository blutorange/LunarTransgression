package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;

import dagger.Module;
import dagger.Provides;

@Module
public class SocketModule {
	@Provides @Singleton
	static ISocketProcessing provideSocketProcessing() {
		return ComponentFactory.getLunarComponent()._socketProcessing();
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.AUTHORIZE)
	static ILunarMessageHandler provideMessageHandlerAuthorize() {
		return ComponentFactory.getLunarComponent()._handlerAuthorize();
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.INVITE)
	static ILunarMessageHandler provideMessageHandlerInvite() {
		return ComponentFactory.getLunarComponent()._handlerInvite();
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.INVITE_ACCEPT)
	static ILunarMessageHandler provideMessageHandlerInviteAccept() {
		return ComponentFactory.getLunarComponent()._handlerInviteAccept();
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.INVITE_RETRACT)
	static ILunarMessageHandler provideMessageHandlerInviteRetract() {
		return ComponentFactory.getLunarComponent()._handlerInviteRetract();
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.INVITE_REJECT)
	static ILunarMessageHandler provideMessageHandlerInviteReject() {
		return ComponentFactory.getLunarComponent()._handlerInviteReject();
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.PREPARE_BATTLE)
	static ILunarMessageHandler provideMessageHandlerPrepareBattle() {
		return ComponentFactory.getLunarComponent()._handlerPrepareBattle();
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.STEP_BATTLE)
	static ILunarMessageHandler provideMessageHandlerStepBattle() {
		return ComponentFactory.getLunarComponent()._handlerStepBattle();
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.LOOT)
	static ILunarMessageHandler provideMessageHandlerLoot() {
		return ComponentFactory.getLunarComponent()._handlerLoot();
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.FETCH_DATA)
	static ILunarMessageHandler provideMessageHandlerFetchMyData() {
		return ComponentFactory.getLunarComponent()._handlerFetchData();
	}
}