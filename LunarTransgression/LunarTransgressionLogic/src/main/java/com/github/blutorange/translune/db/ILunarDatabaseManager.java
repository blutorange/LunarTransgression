package com.github.blutorange.translune.db;

import javax.persistence.PersistenceException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.common.ThrowingConsumer;

public interface ILunarDatabaseManager {

	public static enum ELunarDatabaseManagerMock implements ILunarDatabaseManager {
		INSTANCE;

		@Override
		public void createSchema() throws PersistenceException {
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
		public long count(final EEntityMeta entityMeta) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public <@NonNull T extends AbstractStoredEntity> long count(final Class<T> clazz) {
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
		public <T extends AbstractStoredEntity, @NonNull S extends ModifiableEntity<@NonNull T>, E extends Exception> void modify(
				@NonNull final T entity, final Class<S> modifiableClass, final ThrowingConsumer<S, @NonNull E> modifier)
				throws E {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public <@NonNull T extends AbstractStoredEntity> void persist(final T entity) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public <@NonNull T extends AbstractStoredEntity> void delete(final T entity) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public void shutdown() {
		}

		@Override
		public <@NonNull T extends AbstractStoredEntity> @NonNull T[] findRandom(final Class<@NonNull T> clazz, final int amount) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public <@NonNull T extends AbstractStoredEntity> @NonNull T[] findRandom(final EEntityMeta entityMeta, final int amount) {
			throw new RuntimeException("mock - injection probably failed");
		}

	}

	<T extends AbstractStoredEntity, @NonNull S extends ModifiableEntity<T>, E extends Exception> void modify(
			@NonNull T entity, Class<S> modifiableClass, ThrowingConsumer<S, @NonNull E> modifier) throws E;

	@Nullable
	<@NonNull T extends AbstractStoredEntity> T find(final Class<T> clazz, final Object primaryKey);

	<@NonNull T extends AbstractStoredEntity> T @NonNull [] findAll(final Class<T> clazz, final Object[] primaryKeys);

	@Nullable
	<@NonNull T extends AbstractStoredEntity> T find(final EEntityMeta entityMeta, final Object primaryKey);

	<@NonNull T extends AbstractStoredEntity> T @NonNull [] findAll(final EEntityMeta entityMeta,
			final Object[] primaryKeys);

	long count(final EEntityMeta entityMeta);

	<@NonNull T extends AbstractStoredEntity> long count(final Class<T> clazz);

	void createSchema() throws PersistenceException;

	<@NonNull T extends AbstractStoredEntity> void persist(T entity);

	<@NonNull T extends AbstractStoredEntity> void delete(T entity);

	void shutdown();

	<@NonNull T extends AbstractStoredEntity> T[] findRandom(Class<T> clazz, int amount);

	<@NonNull T extends AbstractStoredEntity> T[] findRandom(EEntityMeta entityMeta, int amount);
}
