package com.github.blutorange.translune.db;

import javax.persistence.PersistenceException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public interface ILunarDatabaseManager {

	public static enum ELunarDatabaseManagerMock implements ILunarDatabaseManager {
		INSTANCE;

		@Override
		public void createSchema() throws PersistenceException {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Nullable
		@Override
		public <@NonNull T extends AbstractEntity> T find(final Class<T> clazz, final Object primaryKey) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		@Nullable
		public <@NonNull T extends AbstractEntity> T find(final EEntityMeta entityMeta, final Object primaryKey) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public long count(final EEntityMeta entityMeta) {
			throw new RuntimeException("mock - injection probably failed");
		}

		@Override
		public <@NonNull T extends AbstractEntity> long count(final Class<T> clazz) {
			throw new RuntimeException("mock - injection probably failed");
		}
	}

	@Nullable
	<@NonNull T extends AbstractEntity> T find(final Class<T> clazz, final Object primaryKey);

	@Nullable
	<@NonNull T extends AbstractEntity> T find(final EEntityMeta entityMeta, final Object primaryKey);

	long count(final EEntityMeta entityMeta);

	<@NonNull T extends AbstractEntity> long count(final Class<T> clazz);

	void createSchema() throws PersistenceException;
}
