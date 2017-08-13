package com.github.blutorange.translune.db;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.eclipse.jdt.annotation.Nullable;

public class EntityStore implements IEntityStore {

	private final Map<EEntityMeta, Map<Object, AbstractEntity>> typeMap;

	public EntityStore(final EntityManagerFactory emf) {
		final Map<EEntityMeta, Map<Object, AbstractEntity>> classMap = new EnumMap<>(EEntityMeta.class);
		emf.getMetamodel().getEntities().forEach(entity -> {
			final Class<?> entityClazz = entity.getJavaType();
			if (!AbstractEntity.class.isAssignableFrom(entityClazz))
				throw new RuntimeException("all entities must extend AbstractEntity");
			@SuppressWarnings("unchecked")
			final Class<? extends AbstractEntity> abstractEntityClazz = (Class<? extends AbstractEntity>)entityClazz;
			final EEntityMeta entityType = EEntityMeta.valueOf(abstractEntityClazz);
			classMap.put(entityType, Collections.synchronizedMap(new HashMap<>()));
		});
		this.typeMap = classMap;
	}

	@Nullable
	@Override
	public <T extends AbstractEntity> T retrieve(final Class<T> clazz, final Object primaryKey) {
		return retrieve(EEntityMeta.valueOf(clazz), primaryKey);
	}

	@Nullable
	@Override
	public <T extends AbstractEntity> T retrieve(final EEntityMeta entityMeta, final Object primaryKey) {
		final Map<Object, AbstractEntity> map = typeMap.get(entityMeta);
		if (map == null) return null;
		return (T)(map.get(primaryKey));
	}


	@Override
	public void store(final AbstractEntity entity) {
		if (entity == null)
			return;
		final Map<Object, AbstractEntity> map = typeMap.get(entity.getEntityMeta());
		if (map == null) return;
		map.put(entity.getPrimaryKey(), entity);
	}

	@Override
	public boolean remove(final AbstractEntity entity) {
		final Map<Object, AbstractEntity> map = typeMap.get(entity.getEntityMeta());
		if (map == null)
			return false;
		return map.remove(entity.getPrimaryKey()) != null;
	}

	@Nullable
	@Override
	public <T extends AbstractEntity> T remove(final Class<T> clazz, final Object primaryKey) {
		return remove(EEntityMeta.valueOf(clazz), primaryKey);
	}

	@Nullable
	@Override
	public <T extends AbstractEntity> T remove(final EEntityMeta entityMeta, final Object primaryKey) {
		@SuppressWarnings("unchecked")
		final Map<Object, T> map = (Map<Object, T>)typeMap.get(entityMeta);
		if (map == null)
			return null;
		return map.remove(primaryKey);
	}
}