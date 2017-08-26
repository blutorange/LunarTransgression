package com.github.blutorange.translune.ic;

import java.io.IOException;

import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;

import com.github.blutorange.common.Suppliers;
import com.github.blutorange.common.ThrowingSupplier;
import com.github.blutorange.translune.db.EntityStore;
import com.github.blutorange.translune.db.IEntityManagerFactory;
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
	static IEntityManagerFactory provideEntityManagerFactory(@Classed(DatabaseModule.class) final Logger logger) {
		final ThrowingSupplier<EntityManagerFactory, IOException> supplier= Suppliers.memoize(() -> {
			logger.info("creating entity manager factory");
			final EntityManagerFactory emf;
			try {
				emf = Persistence.createEntityManagerFactory("hibernate");
			}
			catch (final Exception e) {
				throw new IOException("failed to create entity manager factory", e);
			}
			logger.info("entity manager factory is " + emf.getClass().getCanonicalName());
			return emf;
		});
		return () -> supplier.get();
	}

	@Provides
	@Singleton
	static IEntityStore provideEntityStore() {
		return new EntityStore();
	}

	@Provides
	@Singleton
	static IImportProcessing provideImporProcessing() {
		return ComponentFactory.getLunarComponent()._importProcessing();
	}

	@Provides
	@Singleton
	static ILunarDatabaseManager provideLunarDatabaseManager() {
		final LunarDatabaseManager ldm = new LunarDatabaseManager();
		ComponentFactory.getLunarComponent().inject(ldm);
		return ldm;
	}
}
