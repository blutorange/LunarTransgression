package com.github.blutorange.translune.db;

import org.eclipse.jdt.annotation.Nullable;

public interface IEntityStore {
	@Nullable
	<T extends AbstractStoredEntity> T retrieve(Class<T> clazz, Object primaryKey);
	@Nullable
	<T extends AbstractStoredEntity> T retrieve(EEntityMeta entityMeta, Object primaryKey);

	void store(AbstractStoredEntity entity);
	@Nullable
	<T extends AbstractStoredEntity> T storeIfAbsent(@Nullable T entity);

	/**
	 *
	 * @param entity
	 * @return True iff the entity was removed, false iff the entity was not stored.
	 */
	boolean remove(AbstractStoredEntity entity);

	@Nullable
	<T extends AbstractStoredEntity> T remove(Class<T> clazz, Object primaryKey);
	@Nullable
	public <T extends AbstractStoredEntity> T remove(final EEntityMeta entityMeta, final Object primaryKey);
}