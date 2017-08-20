package com.github.blutorange.translune.db;

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
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.hibernate.Session;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.util.Constants;
import com.github.blutorange.translune.util.ThrowingConsumer;
import com.github.blutorange.translune.util.ThrowingFunction;

@Singleton
public class LunarDatabaseManager implements ILunarDatabaseManager {
	@Inject	@Classed(LunarDatabaseManager.class)
	Logger logger;

	@Inject	EntityManagerFactory entityManagerFactory;

	@Inject IEntityStore entityStore;

	@Inject @Named("default") Scheduler scheduler;

	@Inject @Named("basic")
	Random random;

	protected final List<IChange> changeList;

	@Inject
	public LunarDatabaseManager() {
		changeList = Collections.synchronizedList(new ArrayList<>());
	}

	public void init() {
		try {
			addJobSaveDb();
		}
		catch (final SchedulerException e) {
			throw new RuntimeException("could not create job(s)", e);
		}
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

	private void addJobSaveDb() throws SchedulerException {
		logger.debug("adding job saveDb");
		final JobDetail jobDetail = JobBuilder.newJob(SaveDb.class).withIdentity("jobSaveDb", "db").build();
		final ScheduleBuilder<SimpleTrigger> scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
				.withIntervalInMinutes(Constants.CONFIG_DATABASE_SAVE_MINUTES).repeatForever();
		final Trigger jobTrigger = TriggerBuilder.newTrigger().withIdentity("triggerSaveDb", "db").startNow()
				.withSchedule(scheduleBuilder).build();
		scheduler.scheduleJob(jobDetail, jobTrigger);
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
			saveChangesToDatabase();
			entity = withEm(em -> {
				@SuppressWarnings("unchecked")
				final @Nullable T object = (T) em.find(entityMeta.getJavaClass(), primaryKey);
				entityStore.storeIfAbsent(object);
				if (object != null)
					updateAsscociations(object, new HashSet<>());
				return object;
			});
			return entity;
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <@NonNull T extends AbstractStoredEntity> T[] findRandom(final Class<T> clazz, final int amount) {
		synchronized (entityStore) {
			saveChangesToDatabase();
		}
		return withEm(em -> {
			final CriteriaBuilder cb = em.getCriteriaBuilder();
			final Long longCount = count(em, cb, clazz);
			final int count = longCount != null ? longCount.intValue() : 0;
			if (count < 1)
				return (T[])Array.newInstance(clazz, 0);
			final T[] result = (T[])Array.newInstance(clazz, amount);
			for (int i = amount; i --> 0; ++i) {
				final CriteriaQuery<T> cq = cb.createQuery(clazz);
				cq.select(cq.from(clazz));
				final TypedQuery<T> tq = em.createQuery(cq);
				tq.setFirstResult(random.nextInt(count));
				tq.setMaxResults(1);
				final List<T> resultList = tq.getResultList();
				if (resultList.isEmpty())
					return (T[])Array.newInstance(clazz, 0);
				result[i] = resultList.get(0);
			}
			if (result == null)
				return (T[])Array.newInstance(clazz, 0);
			return result;
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <@NonNull T extends AbstractStoredEntity> T[] findRandom(final EEntityMeta entityMeta, final int amount) {
		return (T[])findRandom(entityMeta.getJavaClass(), amount);
	}


	@Override
	public <@NonNull T extends AbstractStoredEntity> void persist(final T entity) {
		entityStore.store(entity);
		synchronized (entityStore) {
			changeList.add(new ChangePersist(entity));
		}
	}

	@Override
	public <@NonNull T extends AbstractStoredEntity> void delete(final T entity) {
		synchronized (entityStore) {
			entityStore.remove(entity);
			changeList.add(new ChangeDelete(entity));
		}
	}

	@SuppressWarnings("resource")
	protected void saveChangesToDatabase() {
		if (!changeList.isEmpty()) {
			withEm(true, em -> {
				final Session session = em.unwrap(Session.class);
				for (final IChange change : changeList)
					change.perform(em, session);
				return StringUtils.EMPTY;
			});
			changeList.clear();
		}
	}

	private void updateAsscociations(final AbstractStoredEntity object, final Set<AbstractStoredEntity> processed) {
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
				updateAsscociations(fetched, processed);
			}
		});
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
	public <@NonNull T extends AbstractStoredEntity> T[] findAll(final Class<T> clazz, final Object[] primaryKeys) {
		return findAll(EEntityMeta.valueOf(clazz), primaryKeys);
	}

	@Override
	public <@NonNull T extends AbstractStoredEntity> T[] findAll(final EEntityMeta entityMeta, final Object[] primaryKeys) {
		final Class<?> clazz = entityMeta.getJavaClass();
		@SuppressWarnings("unchecked")
		final T[] result = (T[]) Array.newInstance(clazz, primaryKeys.length);
		for (int i = 0; i < primaryKeys.length; ++i) {
			final @Nullable T object = find(entityMeta, primaryKeys[i]);
			if (object == null) {
				@SuppressWarnings("unchecked")
				final T[] emptyReturn = (T[]) Array.newInstance(clazz, 0);
				return emptyReturn;
			}
			result[i] = object;
		}
		return result;
	}

	@Override
	public long count(final EEntityMeta entityMeta) {
		return count(entityMeta.getJavaClass());
	}

	@Override
	public <@NonNull T extends AbstractStoredEntity> long count(final Class<T> clazz) {
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

	public static class SaveDb implements Job {
		@Override
		public void execute(final JobExecutionContext context) throws JobExecutionException {
			LunarDatabaseManager ldm;
			try {
				ldm = (LunarDatabaseManager)(ComponentFactory.getDatabaseComponent().iLunarDatabaseManager());
			}
			catch (final ClassCastException e) {
				throw new JobExecutionException("failed to acquire lunar database manager", e);
			}
			if (ldm == null || ldm.changeList.isEmpty())
				return;
			synchronized (ldm.entityStore) {
				ldm.saveChangesToDatabase();
			}
		}
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
	}
}