package com.github.blutorange.translune.db;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.eclipse.jdt.annotation.Nullable;

public class EntityStore implements IEntityStore {

	private final Map<EEntityMeta, Map<Object, AbstractStoredEntity>> typeMap;

	public EntityStore(final EntityManagerFactory emf) {
		final Map<EEntityMeta, Map<Object, AbstractStoredEntity>> classMap = new EnumMap<>(EEntityMeta.class);
		emf.getMetamodel().getEntities().forEach(entity -> {
			final Class<?> entityClazz = entity.getJavaType();
			if (!AbstractStoredEntity.class.isAssignableFrom(entityClazz))
				return;
			@SuppressWarnings("unchecked")
			final Class<? extends AbstractStoredEntity> abstractEntityClazz = (Class<? extends AbstractStoredEntity>)entityClazz;
			final EEntityMeta entityType = EEntityMeta.valueOf(abstractEntityClazz);
			classMap.put(entityType, Collections.synchronizedMap(new HashMap<>()));
		});
		this.typeMap = classMap;
	}

	@Nullable
	@Override
	public <T extends AbstractStoredEntity> T retrieve(final Class<T> clazz, final Object primaryKey) {
		return retrieve(EEntityMeta.valueOf(clazz), primaryKey);
	}

	@Nullable
	@Override
	public <T extends AbstractStoredEntity> T retrieve(final EEntityMeta entityMeta, final Object primaryKey) {
		final Map<Object, AbstractStoredEntity> map = typeMap.get(entityMeta);
		if (map == null) return null;
		return (T)(map.get(primaryKey));
	}

	@Override
	public void store(final AbstractStoredEntity entity) {
		if (entity == null)
			return;
		final Map<Object, AbstractStoredEntity> map = typeMap.get(entity.getEntityMeta());
		if (map == null) return;
		map.put(entity.getPrimaryKey(), entity);
	}

	@Override
	@Nullable
	public <T extends AbstractStoredEntity> T storeIfAbsent(@Nullable final T entity) {
		if (entity == null)
			return null;
		final Map<Object, AbstractStoredEntity> map = typeMap.get(entity.getEntityMeta());
		if (map == null)
			return null;
		return (T) map.putIfAbsent(entity.getPrimaryKey(), entity);
	}

	@Override
	public boolean remove(final AbstractStoredEntity entity) {
		final Map<Object, AbstractStoredEntity> map = typeMap.get(entity.getEntityMeta());
		if (map == null)
			return false;
		return map.remove(entity.getPrimaryKey()) != null;
	}

	@Nullable
	@Override
	public <T extends AbstractStoredEntity> T remove(final Class<T> clazz, final Object primaryKey) {
		return remove(EEntityMeta.valueOf(clazz), primaryKey);
	}

	@Nullable
	@Override
	public <T extends AbstractStoredEntity> T remove(final EEntityMeta entityMeta, final Object primaryKey) {
		@SuppressWarnings("unchecked")
		final Map<Object, T> map = (Map<Object, T>)typeMap.get(entityMeta);
		if (map == null)
			return null;
		return map.remove(primaryKey);
	}
}