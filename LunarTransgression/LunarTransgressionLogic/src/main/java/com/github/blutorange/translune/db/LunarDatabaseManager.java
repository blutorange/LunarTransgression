package com.github.blutorange.translune.db;

import java.util.Properties;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.util.ThrowingFunction;

public class LunarDatabaseManager implements ILunarDatabaseManager {
	private final Logger logger;

	private final EntityManagerFactory entityManagerFactory;

	private final IEntityStore entityStore;

	@Inject
	public LunarDatabaseManager(@Classed(LunarDatabaseManager.class) final Logger logger,
			final EntityManagerFactory entityManagerFactory, final IEntityStore entityStore) {
		this.entityManagerFactory = entityManagerFactory;
		this.entityStore = entityStore;
		this.logger = logger;
	}

	@Override
	@Nullable
	public <@NonNull T extends AbstractEntity> T find(final Class<T> clazz, final Object primaryKey) {
		return find(EEntityMeta.valueOf(clazz), primaryKey);
	}

	@Override
	@Nullable
	public <@NonNull T extends AbstractEntity> T find(final EEntityMeta entityMeta, final Object primaryKey) {
		@Nullable
		T entity = entityStore.retrieve(entityMeta, primaryKey);
		if (entity != null)
			return entity;
		entity = (T) withEm(em -> em.find(entityMeta.getJavaClass(), primaryKey));
		entityStore.store(entity);
		return entity;
	}

	@Nullable
	private <T> T withEm(final ThrowingFunction<EntityManager, T, Exception> runnable) {
		return withEm(false, runnable);
	}

	@Override
	public void createSchema() throws PersistenceException {
		final Properties p = new Properties();
		p.put("javax.persistence.schema-generation.database.action", "drop-and-create");
		Persistence.generateSchema("hibernate", p);
	}

	@Override
	public long count(final EEntityMeta entityMeta) {
		return count(entityMeta.getJavaClass());
	}

	@Override
	public <@NonNull T extends AbstractEntity> long count(final Class<T> clazz) {
		final Long count = withEm(em -> {
			final CriteriaBuilder cb = em.getCriteriaBuilder();
			final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			return em.createQuery(cq.select(cb.count(cq.from(clazz)))).getSingleResult();
		});
		return count != null ? count.longValue() : 0;
	}

	@Nullable
	private <T> T withEm(final boolean transactional, final ThrowingFunction<EntityManager, T, Exception> runnable) {
		logger.debug("opening entity manager");
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			if (transactional)
				em.getTransaction().begin();
			return runnable.apply(em);
		}
		catch (final Exception e) {
			logger.error("failed to execute action with entity manager", e);
			if (em.getTransaction().isActive()) {
				try {
					em.getTransaction().rollback();
				}
				catch (final Exception e2) {
					logger.error("failed to rollback transaction", e2);
				}
			}
			return null;
		}
		finally {
			if (em.getTransaction().isActive()) {
				try {
					em.getTransaction().commit();
				}
				catch (final Exception e) {
					logger.error("failed to commit transaction", e);
				}
			}
			if (em.isOpen()) {
				try {
					em.close();
				}
				catch (final Exception e) {
					logger.error("failed to close entity manager", e);
				}
			}
		}
	}
}