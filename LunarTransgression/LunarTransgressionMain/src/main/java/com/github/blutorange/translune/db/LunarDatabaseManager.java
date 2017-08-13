package com.github.blutorange.translune.db;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.util.ThrowingFunction;

public class LunarDatabaseManager implements ILunarDatabaseManager {
	@Inject @Classed(LunarDatabaseManager.class) Logger logger;

	private final EntityManagerFactory entityManagerFactory;

	private EntityManager entityManager;

	@Inject
	public LunarDatabaseManager(final EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	@Override
	@Nullable
	public <T> T find(final Class<T> clazz, final Object primaryKey) {
		return withEm(em -> {
			return em.<T>find(clazz, primaryKey);
		});
	}

	@Nullable
	private <T> T withEm(final ThrowingFunction<EntityManager, T, Exception> runnable) {
		EntityManager em = this.entityManager;
		if (em == null || !em.isOpen()) {
			synchronized (this) {
				if (entityManager == null || !entityManager.isOpen()) {
					entityManager = entityManagerFactory.createEntityManager();
				}
				em = entityManager;
			}
		}
		try {
			return runnable.apply(em);
		}
		catch (final Exception e) {
			logger.error("Failed to execute action with entity manager", e);
			return null;
		}
	}
}