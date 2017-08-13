package com.github.blutorange.translune.db;

import org.eclipse.jdt.annotation.Nullable;

public interface IEntityStore {
	@Nullable
	<T extends AbstractEntity> T retrieve(Class<T> clazz, Object primaryKey);
	@Nullable
	<T extends AbstractEntity> T retrieve(EEntityMeta entityMeta, Object primaryKey);

	void store(AbstractEntity entity);
	/**
	 *
	 * @param entity
	 * @return True iff the entity was removed, false iff the entity was not stored.
	 */
	boolean remove(AbstractEntity entity);

	@Nullable
	<T extends AbstractEntity> T remove(Class<T> clazz, Object primaryKey);
	@Nullable
	public <T extends AbstractEntity> T remove(final EEntityMeta entityMeta, final Object primaryKey);
}