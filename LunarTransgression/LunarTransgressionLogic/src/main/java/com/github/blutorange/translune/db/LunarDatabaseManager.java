package com.github.blutorange.translune.db;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.hibernate.Session;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.slf4j.Logger;

import com.github.blutorange.common.ThrowingConsumer;
import com.github.blutorange.common.ThrowingFunction;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.IRandomSupplier;

@Singleton
public class LunarDatabaseManager implements ILunarDatabaseManager {
	@Inject	@Classed(LunarDatabaseManager.class)
	Logger logger;

	@Inject	IEntityManagerFactory entityManagerFactory;

	@Inject IEntityStore entityStore;

	@Inject @Named("default") Scheduler scheduler;

	@Inject @Named("basic")
	IRandomSupplier random;

	protected final List<IChange> changeList;

	@Inject
	public LunarDatabaseManager() {
		changeList = Collections.synchronizedList(new ArrayList<>());
	}

	@Override
	public void shutdown() {
		try {
			synchronized (entityStore) {
				saveChangesToDatabase();
			}
		}
		catch (final Exception e) {
			logger.error("failed to save pending data to the database", e);
		}
	}

	@Override
	@Nullable
	public <@NonNull T extends AbstractStoredEntity> T find(final Class<T> clazz, final Object primaryKey) {
		return find(EEntityMeta.valueOf(clazz), primaryKey);
	}

	@Override
	@Nullable
	public <@NonNull T extends AbstractStoredEntity> T find(final EEntityMeta entityMeta, final Object primaryKey) {
		@Nullable
		T entity = entityStore.retrieve(entityMeta, primaryKey);
		if (entity != null)
			return entity;
		synchronized (entityStore) {
			@Nullable
			final T e = entityStore.retrieve(entityMeta, primaryKey);
			if (e != null)
				return e;
			try {
				saveChangesToDatabase();
			}
			catch (final IOException ex) {
				logger.error("could not perform find, failed to write changes to database", ex);
				return null;
			}
			entity = withEm(em -> {
				@SuppressWarnings("unchecked")
				final @Nullable T object = (T) em.find(entityMeta.getJavaClass(), primaryKey);
				entityStore.storeIfAbsent(object);
				if (object != null)
					attachAsscociations(object, new HashSet<>());
				return object;
			});
			return entity;
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <@NonNull T extends AbstractStoredEntity> T[] findRandom(final Class<T> clazz, final int amount) throws IOException {
		synchronized (entityStore) {
			saveChangesToDatabase();
		}
		if (amount < 1) {
			return (T[])Array.newInstance(clazz, 0);
		}
		return withEm(em -> {
			final CriteriaBuilder cb = em.getCriteriaBuilder();
			final Long longCount = count(em, cb, clazz);
			final int count = longCount != null ? longCount.intValue() : 0;
			if (count < 1)
				return (T[])Array.newInstance(clazz, 0);
			final T[] result = (T[])Array.newInstance(clazz, amount);
			final Random r = random.get();
			for (int i = amount; i --> 0;) {
				final CriteriaQuery<T> cq = cb.createQuery(clazz);
				cq.select(cq.from(clazz));
				final TypedQuery<T> tq = em.createQuery(cq);
				tq.setFirstResult(r.nextInt(count));
				tq.setMaxResults(1);
				final List<T> resultList = tq.getResultList();
				if (resultList.isEmpty())
					return (T[])Array.newInstance(clazz, 0);
				@Nullable final T object = resultList.get(0);
				if (object == null)
					return (T[])Array.newInstance(clazz, 0);
				result[i] = object;
			}
			if (result == null)
				return (T[])Array.newInstance(clazz, 0);
			return result;
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <@NonNull T extends AbstractStoredEntity> T[] findRandom(final EEntityMeta entityMeta, final int amount)  throws IOException{
		return (T[])findRandom(entityMeta.getJavaClass(), amount);
	}

	@Override
	public void checkConnection() throws Exception {
		EntityManager em = null;
		try {
			em = entityManagerFactory.get().createEntityManager();
			em.find(Player.class, "");
		}
		finally {
			if (em != null)
				em.close();
		}
	}

	@Override
	public <@NonNull T extends AbstractStoredEntity> ILunarDatabaseManager persist(final T entity) {
		logger.debug("persisting entity " + entity.getPrimaryKey());
		synchronized (entityStore) {
			entityStore.store(entity);
			changeList.add(new ChangePersist(entity));
		}
		return this;
	}

	@SafeVarargs
	@Override
	public final <@NonNull T extends AbstractStoredEntity> ILunarDatabaseManager persist(final T... entities) {
		synchronized (entityStore) {
			entityStore.store(entities);
			for (final T entity : entities) {
				logger.debug("persisting entity " + entity.getPrimaryKey());
				changeList.add(new ChangePersist(entity));
			}
		}
		return this;
	}

	@Override
	public <@NonNull T extends AbstractStoredEntity> ILunarDatabaseManager delete(final T entity) {
		synchronized (entityStore) {
			entityStore.remove(entity);
			changeList.add(new ChangeDelete(entity));
		}
		return this;
	}

	@SuppressWarnings("resource")
	private void saveChangesToDatabase() throws IOException {
		logger.info("flushing entity changes to database");
		if (logger.isDebugEnabled()) {
			logger.debug("flushing entity changes to database");
			changeList.forEach(change -> logger.debug("need to process change " + change));
		}
		if (!changeList.isEmpty()) {
			final Boolean result = withEm(true, em -> {
				final Session session = em.unwrap(Session.class);
				for (final IChange change : changeList)
					change.perform(em, session);
				return Boolean.TRUE;
			});
			if (result == null)
				throw new IOException("failed to save changes to the database");
			changeList.clear();
		}
	}

	private void attachAsscociations(final AbstractStoredEntity object, final Set<AbstractStoredEntity> processed) {
		object.forEachAssociatedObject(accesible -> {
			final AbstractStoredEntity fetched = accesible.get();
			AbstractStoredEntity currentlyStored = entityStore.storeIfAbsent(fetched);
			if (currentlyStored != null)
				accesible.set(currentlyStored);
			else {
				currentlyStored = fetched;
			}
			if (!processed.contains(currentlyStored)) {
				processed.add(currentlyStored);
				attachAsscociations(fetched, processed);
			}
		});
	}

	private void detachAsscociations(final AbstractStoredEntity object, final Set<AbstractStoredEntity> processed) {
		object.forEachAssociatedObject(accesible -> {
			final AbstractStoredEntity fetched = accesible.get();
			if (fetched == null)
				return;
			entityStore.remove(fetched);
			if (!processed.contains(fetched)) {
				processed.add(fetched);
				detachAsscociations(fetched, processed);
			}
		});
	}

	@Nullable
	private <@Nullable T> T withEm(final ThrowingFunction<EntityManager, T, Exception> runnable) {
		return withEm(false, runnable);
	}

	@Override
	public void createSchema() throws PersistenceException {
		final Properties p = new Properties();
		p.put("javax.persistence.schema-generation.database.action", "drop-and-create");
		Persistence.generateSchema("hibernate", p);
	}

	@Override
	public <@NonNull T extends AbstractStoredEntity> T[] findAll(final Class<T> clazz, final Object[] primaryKeys)  throws IOException{
		return findAll(EEntityMeta.valueOf(clazz), primaryKeys);
	}

	@Override
	public <@NonNull T extends AbstractStoredEntity> T[] findAll(final EEntityMeta entityMeta, final Object[] primaryKeys)  throws IOException{
		final Class<?> clazz = entityMeta.getJavaClass();
		@SuppressWarnings({ "unchecked", "null" })
		final T@NonNull[] result = (T[]) Array.newInstance(clazz, primaryKeys.length);
		for (int i = 0; i < primaryKeys.length; ++i) {
			final @Nullable T object = find(entityMeta, primaryKeys[i]);
			if (object == null) {
				@SuppressWarnings({ "unchecked", "null" })
				final T@NonNull[] emptyReturn = (T[]) Array.newInstance(clazz, 0);
				return emptyReturn;
			}
			result[i] = object;
		}
		return result;
	}

	@Override
	public long count(final EEntityMeta entityMeta) throws IOException {
		return count(entityMeta.getJavaClass());
	}

	@Override
	public <@NonNull T extends AbstractStoredEntity> long count(final Class<T> clazz) throws IOException {
		synchronized (entityStore) {
			saveChangesToDatabase();
		}
		final Long count = withEm(em -> {
			final CriteriaBuilder cb = em.getCriteriaBuilder();
			return count(em, cb, clazz);
		});
		return count != null ? count.longValue() : 0;
	}

	@Nullable
	private <@NonNull T extends AbstractStoredEntity> Long count(final EntityManager em, final CriteriaBuilder cb, final Class<T> clazz) {
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		return em.createQuery(cq.select(cb.count(cq.from(clazz)))).getSingleResult();
	}

	@Nullable
	@Override
	public <@Nullable T> T withEm(final boolean transactional, final ThrowingFunction<@NonNull EntityManager, T, Exception> runnable) {
		logger.debug("opening entity manager");
		final EntityManager em;
		try {
			em = entityManagerFactory.get().createEntityManager();
		}
		catch (final IOException e) {
			logger.error("failed to get entity manager factory", e);
			return null;
		}
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

	@Override
	public <T extends AbstractStoredEntity, @NonNull S extends ModifiableEntity<@NonNull T>, E extends Exception> void modify(
			@NonNull final T entity, final Class<S> modifiableClass, final ThrowingConsumer<S, @NonNull E> modifier)
			throws E {
		S modifiable;
		try {
			modifiable = modifiableClass.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e) {
			logger.error("failed to construct modifiable", e);
			throw new RuntimeException("failed to construct modifiable", e);
		}
		modifiable.setEntity(entity);
		synchronized (entityStore) {
			changeList.add(new ChangeModify(entity));
			modifier.accept(modifiable);
		}
	}

	@Override
	public <@NonNull T extends AbstractStoredEntity> ILunarDatabaseManager detach(final Class<@NonNull T> clazz, final String primaryKey) {
		synchronized (entityStore) {
			@Nullable final T entity = entityStore.remove(clazz, primaryKey);
			if (entity != null)
				detachAsscociations(entity, new HashSet<>());
		}
		return this;
	}

	@Override
	public <@NonNull T extends AbstractStoredEntity> ILunarDatabaseManager detach(final T entity) {
		synchronized (entityStore) {
			entityStore.remove(entity);
			detachAsscociations(entity, new HashSet<>());
		}
		return this;
	}

	protected static interface IChange {
		void perform(EntityManager em, Session session);
	}

	protected static class ChangePersist implements IChange {

		private final AbstractStoredEntity entity;

		public ChangePersist(final AbstractStoredEntity entity) {
			this.entity = entity;
		}

		@Override
		public void perform(final EntityManager em, final Session session) {
			session.persist(entity);
		}

		@Override
		public String toString() {
			return String.format("ChangePersist(%s)", entity.getPrimaryKey());
		}
	}

	protected static class ChangeModify implements IChange {
		private final AbstractStoredEntity entity;

		public ChangeModify(final AbstractStoredEntity entity) {
			this.entity = entity;
		}

		@Override
		public void perform(final EntityManager em, final Session session) {
			session.update(entity);
		}

		@Override
		public String toString() {
			return String.format("ChangeModify(%s)", entity.getPrimaryKey());
		}
	}

	protected static class ChangeDelete implements IChange {
		private final AbstractStoredEntity entity;

		public ChangeDelete(final AbstractStoredEntity entity) {
			this.entity = entity;
		}

		@Override
		public void perform(final EntityManager em, final Session session) {
			if (!session.contains(entity))
				session.saveOrUpdate(entity);
			session.delete(entity);
		}

		@Override
		public String toString() {
			return String.format("ChangeDelete(%s)", entity.getPrimaryKey());
		}
	}

	@Override
	public void flushAndEmpty() throws IOException {
		logger.debug("number of changes to process " + changeList.size());
		synchronized (entityStore) {
			saveChangesToDatabase();
			entityStore.clear();
		}
	}

	@Override
	public void runPeriodically() throws JobExecutionException {
		logger.debug("number of changes to process " + changeList.size());
		if (changeList.isEmpty())
			return;
		synchronized (entityStore) {
			try {
				saveChangesToDatabase();
			}
			catch (final IOException e) {
				throw new JobExecutionException("could not write changes to database", e);
			}
		}
	}

	@Override
	public void persistResource(final String name, final byte @NonNull [] data, final String mime, final String filename) throws IOException {
		final Resource resource = new Resource();
		resource.setData(data);
		resource.setFilename(filename);
		resource.setMime(mime);
		resource.setName(name);
		persistNonManaged(resource);
	}

	@Override
	public void persistNonManaged(final AbstractUnstoredEntity... entities) throws IOException {
		final Object result = withEm(true, em -> {
			for (final AbstractEntity entity : entities)
				em.persist(entity);
			return Boolean.TRUE;
		});
		if (result == null)
			throw new IOException("failed to persist entity");
	}
}