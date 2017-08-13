package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import com.github.blutorange.translune.logic.ISessionStore;
import com.github.blutorange.translune.socket.LunarDecoder;
import com.github.blutorange.translune.socket.LunarEncoder;
import com.github.blutorange.translune.socket.LunarEndpoint;
import com.github.blutorange.translune.socket.TestEndpoint;
import com.github.blutorange.translune.socket.handler.HandlerAuthorize;
import com.github.blutorange.translune.socket.handler.HandlerInvite;
import com.github.blutorange.translune.socket.handler.HandlerInviteAccept;

import dagger.Component;

@Singleton
@Component(modules={LoggingModule.class, SocketModule.class, StorageModule.class})
public interface SocketComponent {
	void inject(LunarEndpoint injectable);
	void inject(LunarDecoder injectable);
	void inject(LunarEncoder sessionBean);
	void inject(TestEndpoint sessionBean);
	ISessionStore sessionStore();
	HandlerAuthorize handlerAuthorization();
	HandlerInvite handlerInvite();
	HandlerInviteAccept handlerInviteAccept();
}