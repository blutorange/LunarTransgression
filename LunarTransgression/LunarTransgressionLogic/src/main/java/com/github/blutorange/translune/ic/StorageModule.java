package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import org.slf4j.Logger;

import com.github.blutorange.translune.logic.BattleStore;
import com.github.blutorange.translune.logic.IBattleStore;
import com.github.blutorange.translune.logic.IInitIdStore;
import com.github.blutorange.translune.logic.IInvitationStore;
import com.github.blutorange.translune.logic.ISessionStore;
import com.github.blutorange.translune.logic.InitIdStore;
import com.github.blutorange.translune.logic.InvitationStore;
import com.github.blutorange.translune.logic.SessionStore;

import dagger.Module;
import dagger.Provides;

@Module
public class StorageModule {
	@Provides @Singleton static IInitIdStore provideInitIdStore() {
		return new InitIdStore();
	}
	@Provides @Singleton static ISessionStore provideSessionStore() {
		return new SessionStore();
	}
	@Provides @Singleton static IInvitationStore provideInvitationStore() {
		return new InvitationStore();
	}
	@Provides @Singleton static IBattleStore provideBattleStore(@Classed(BattleStore.class) final Logger logger) {
		return new BattleStore(logger);
	}
}