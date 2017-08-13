package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import org.slf4j.Logger;

import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.SocketProcessing;

import dagger.Module;
import dagger.Provides;

@Module
public class SocketModule {
	@Provides @Singleton static ISocketProcessing provideSocketProcessing(@Classed(SocketProcessing.class) final Logger logger) {
		return new SocketProcessing(logger);
	}
}