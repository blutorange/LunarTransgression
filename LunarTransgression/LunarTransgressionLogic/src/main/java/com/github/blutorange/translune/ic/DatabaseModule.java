package com.github.blutorange.translune.ic;

import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;

import com.github.blutorange.translune.db.EntityStore;
import com.github.blutorange.translune.db.IEntityStore;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.LunarDatabaseManager;
import com.github.blutorange.translune.serial.IImportProcessing;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {
	@Provides
	@Singleton
	static EntityManagerFactory provideEntityManagerFactory(@Classed(DatabaseModule.class) final Logger logger) {
		logger.info("creating entity manager factory");
		final EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate");
		logger.info("entity manager factory is " + emf.getClass().getCanonicalName());
		return emf;
	}

	@Provides @Singleton static IEntityStore provideEntityStore(final EntityManagerFactory emf) {
		return new EntityStore(emf);
	}

	@Provides @Singleton
	static IImportProcessing provideImporProcessing() {
		return ComponentFactory.getDatabaseComponent().importProcessing();
	}

	@Provides @Singleton
	static ILunarDatabaseManager provideLunarDatabaseManager() {
		final LunarDatabaseManager ldm  = new LunarDatabaseManager();
		ComponentFactory.getDatabaseComponent().inject(ldm);
		return ldm;
	}
}
