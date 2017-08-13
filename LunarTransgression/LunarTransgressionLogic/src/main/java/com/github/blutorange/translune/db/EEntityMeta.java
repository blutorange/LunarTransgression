package com.github.blutorange.translune.db;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum EEntityMeta {
	CHARACTER(Character.class),
	CHARACTER_STATE(CharacterState.class),
	ITEM(Item.class),
	PLAYER(Player.class),
	SKILL(Skill.class);

	private final static Map<Class<? extends AbstractEntity>, EEntityMeta> map;

	static {
		map = Arrays.stream(EEntityMeta.values()).collect(Collectors.toMap(EEntityMeta::getJavaClass, Function.identity()));
	}

	private Class<? extends AbstractEntity> clazz;

	private EEntityMeta(final Class<? extends AbstractEntity> clazz) {
		this.clazz = clazz;
	}

	public Class<? extends AbstractEntity> getJavaClass() {
		return clazz;
	}

	public static EEntityMeta valueOf(final Class<? extends AbstractEntity> entityClazz) {
		final EEntityMeta entityMeta = map.get(entityClazz);
		if (entityClazz == null)
			throw new IllegalArgumentException("no such EEntityMeta for class " + entityClazz);
		return entityMeta;
	}
}