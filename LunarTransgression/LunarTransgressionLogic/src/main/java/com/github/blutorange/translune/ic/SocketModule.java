package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import org.slf4j.Logger;

import com.github.blutorange.translune.handler.HandlerAuthorize;
import com.github.blutorange.translune.handler.HandlerInvite;
import com.github.blutorange.translune.handler.HandlerInviteAccept;
import com.github.blutorange.translune.handler.HandlerInviteReject;
import com.github.blutorange.translune.handler.HandlerInviteRetract;
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
		final HandlerAuthorize handler = new HandlerAuthorize();
		InjectionUtil.inject(handler);
		return handler;
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.INVITE)
	static ILunarMessageHandler provideMessageHandlerInvite() {
		final HandlerInvite handler = new HandlerInvite();
		InjectionUtil.inject(handler);
		return handler;
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.INVITE_ACCEPT)
	static ILunarMessageHandler provideMessageHandlerInviteAccept() {
		final HandlerInviteAccept handler = new HandlerInviteAccept();
		InjectionUtil.inject(handler);
		return handler;
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.INVITE_RETRACT)
	static ILunarMessageHandler provideMessageHandlerInviteRetract() {
		final HandlerInviteRetract handler = new HandlerInviteRetract();
		InjectionUtil.inject(handler);
		return handler;
	}

	@Provides @Singleton @LunarMessageTyped(ELunarMessageType.INVITE_REJECT)
	static ILunarMessageHandler provideMessageHandlerInviteReject() {
		final HandlerInviteReject handler = new HandlerInviteReject();
		InjectionUtil.inject(handler);
		return handler;
	}
}