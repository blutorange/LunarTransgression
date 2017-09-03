package com.github.blutorange.translune.db;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.quartz.JobExecutionException;

import com.github.blutorange.common.ThrowingConsumer;
import com.github.blutorange.common.ThrowingFunction;

public interface ILunarDatabaseManager {

	public static enum ELunarDatabaseManagerMock implements ILunarDatabaseManager {
		INSTANCE;

		@Override
		public void checkConnection() throws Exception {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public <@NonNull T extends AbstractStoredEntity> long count(final Class<T> clazz) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public long count(final EEntityMeta entityMeta) throws IOException {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public void createSchema() throws PersistenceException {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public <@NonNull T extends AbstractStoredEntity> ILunarDatabaseManager delete(final T entity) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public <@NonNull T extends AbstractStoredEntity> ILunarDatabaseManager detach(final Class<@NonNull T> clazz, final String nickname) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public <@NonNull T extends AbstractStoredEntity> ILunarDatabaseManager detach(final T entity) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Nullable
		@Override
		public <@NonNull T extends AbstractStoredEntity> T find(final Class<T> clazz, final Object primaryKey) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		@Nullable
		public <@NonNull T extends AbstractStoredEntity> T find(final EEntityMeta entityMeta, final Object primaryKey) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public <@NonNull T extends AbstractStoredEntity> T[] findAll(final Class<T> clazz, final Object[] primaryKey) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public <@NonNull T extends AbstractStoredEntity> T[] findAll(final EEntityMeta entityMeta,
				final Object[] primaryKey) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public <@NonNull T extends AbstractStoredEntity> @NonNull T[] findRandom(final Class<@NonNull T> clazz,
				final int amount) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public <@NonNull T extends AbstractStoredEntity> @NonNull T[] findRandom(final EEntityMeta entityMeta,
				final int amount) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public void flushAndEmpty() throws IOException {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public <T extends AbstractStoredEntity, @NonNull S extends ModifiableEntity<@NonNull T>, E extends Exception> void modify(
				@NonNull final T entity, final Class<S> modifiableClass, final ThrowingConsumer<S, @NonNull E> modifier)
				throws E {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public <@NonNull T extends AbstractStoredEntity> ILunarDatabaseManager persist(final T entity) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@SafeVarargs
		@Override
		public final <@NonNull T extends AbstractStoredEntity> ILunarDatabaseManager persist(final T... entities) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public void runPeriodically() {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public void shutdown() {
			// not started
		}

		@Override
		public <@Nullable T> @Nullable T withEm(final boolean transactional,
				final ThrowingFunction<@NonNull EntityManager, T, Exception> runnable) {
			throw new RuntimeException("mock - injection probably failed");
		}
	}

	void checkConnection() throws Exception;

	<@NonNull T extends AbstractStoredEntity> long count(final Class<T> clazz) throws IOException;

	long count(final EEntityMeta entityMeta) throws IOException;

	void createSchema() throws PersistenceException;

	<@NonNull T extends AbstractStoredEntity> ILunarDatabaseManager delete(T entity);

	<@NonNull T extends AbstractStoredEntity> ILunarDatabaseManager detach(Class<T> clazz, String nickname);

	<@NonNull T extends AbstractStoredEntity> ILunarDatabaseManager detach(T entity);

	@Nullable
	<@NonNull T extends AbstractStoredEntity> T find(final Class<T> clazz, final Object primaryKey);

	@Nullable
	<@NonNull T extends AbstractStoredEntity> T find(final EEntityMeta entityMeta, final Object primaryKey);

	<@NonNull T extends AbstractStoredEntity> T @NonNull [] findAll(final Class<T> clazz, final Object[] primaryKeys) throws IOException;

	<@NonNull T extends AbstractStoredEntity> T @NonNull [] findAll(final EEntityMeta entityMeta,
			final Object[] primaryKeys) throws IOException;

	<@NonNull T extends AbstractStoredEntity> T[] findRandom(Class<T> clazz, int amount) throws IOException;

	<@NonNull T extends AbstractStoredEntity> T[] findRandom(EEntityMeta entityMeta, int amount) throws IOException;

	void flushAndEmpty() throws IOException;

	<T extends AbstractStoredEntity, @NonNull S extends ModifiableEntity<T>, E extends Exception> void modify(
			@NonNull T entity, Class<S> modifiableClass, ThrowingConsumer<S, @NonNull E> modifier) throws E;

	<@NonNull T extends AbstractStoredEntity> ILunarDatabaseManager persist(T entity);

	<@NonNull T extends AbstractStoredEntity> ILunarDatabaseManager persist(@SuppressWarnings("unchecked") T... entities);

	void runPeriodically() throws JobExecutionException;

	void shutdown();

	@Nullable
	<@Nullable T> T withEm(boolean transactional, ThrowingFunction<@NonNull EntityManager, T, Exception> runnable);
}
