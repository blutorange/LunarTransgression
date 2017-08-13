package com.github.blutorange.translune.ic;

import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.LunarDatabaseManager;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {
	@Provides @Singleton static EntityManagerFactory provideEntityManagerFactory(@Classed(DatabaseModule.class) final Logger logger) {
		logger.info("creating entity manager factory");
		final EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate");
		logger.info("entity manager factory is " + emf.getClass().getCanonicalName());
		return emf;
	}

	@Provides @Singleton static ILunarDatabaseManager provideLunarDatabaseManager(@Classed(DatabaseModule.class) final Logger logger, final EntityManagerFactory emf) {
		logger.info("creating lunar database manager");
		return new LunarDatabaseManager(emf);
	}
}
