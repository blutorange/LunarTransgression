package com.github.blutorange.translune.db;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;

public enum EEntityMeta {
	CHARACTER(Character.class),
	CHARACTER_STATE(CharacterState.class),
	ITEM(Item.class),
	PLAYER(Player.class),
	SKILL(Skill.class);

	private final static Map<Class<? extends AbstractStoredEntity>, EEntityMeta> map;

	static {
		map = Arrays.stream(EEntityMeta.values()).collect(Collectors.toMap(EEntityMeta::getJavaClass, Function.identity()));
	}

	private Class<? extends AbstractStoredEntity> clazz;

	private EEntityMeta(final Class<? extends AbstractStoredEntity> clazz) {
		this.clazz = clazz;
	}

	@SuppressWarnings("null")
	public Class<? extends @NonNull AbstractStoredEntity> getJavaClass() {
		return clazz;
	}

	public static EEntityMeta valueOf(final Class<? extends AbstractStoredEntity> entityClazz) {
		final EEntityMeta entityMeta = map.get(entityClazz);
		if (entityClazz == null)
			throw new IllegalArgumentException("no such EEntityMeta for class " + entityClazz);
		return entityMeta;
	}
}