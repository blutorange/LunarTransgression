package com.github.blutorange.translune.ic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.logic.IBattleStore;
import com.github.blutorange.translune.logic.IInitIdStore;
import com.github.blutorange.translune.logic.IInvitationStore;
import com.github.blutorange.translune.logic.ISessionStore;
import com.github.blutorange.translune.serial.IJsoniter;
import com.github.blutorange.translune.serial.IJsoniter.IJsoniterSupplier;
import com.github.blutorange.translune.serial.JsoniterConfig;
import com.github.blutorange.translune.util.CustomProperties;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;

import dagger.Module;
import dagger.Provides;

@Module
public class StorageModule {
	@Provides @Singleton static IInitIdStore provideInitIdStore() {
		return ComponentFactory.getLunarComponent()._initIdStore();
	}
	@Provides @Singleton static ISessionStore provideSessionStore() {
		return ComponentFactory.getLunarComponent()._sessionStore();
	}
	@Provides @Singleton static IInvitationStore provideInvitationStore() {
		return ComponentFactory.getLunarComponent()._invitationStore();
	}
	@Provides @Singleton static IBattleStore provideBattleStore() {
		return ComponentFactory.getLunarComponent().battleStore();
	}
	@Provides @Singleton static ExecutorService provideExecutorService(final CustomProperties customProperties) {
		return Executors.newFixedThreadPool(customProperties.getMaxThreadCount());
	}

	@Provides @Singleton IJsoniterSupplier provideJsonDeserializer() {
		return ThreadLocal.<IJsoniter>withInitial(() -> new IJsoniterImpl())::get;
	}

	protected static class IJsoniterImpl implements IJsoniter {
		public IJsoniterImpl() {
			synchronized (JsoniterConfig.class) {
				JsoniterConfig.doSetup();
			}
		}
		@Override
		public String serialize(final Object object) {
			return JsonStream.serialize(object);
		}

		@Nullable
		@Override
		public <T> T deserialize(final String input, final Class<T> clazz) {
			return JsonIterator.deserialize(input, clazz);
		}
		@Override
		public Any deserialize(final String data) {
			return JsonIterator.deserialize(data);
		}
	}
}