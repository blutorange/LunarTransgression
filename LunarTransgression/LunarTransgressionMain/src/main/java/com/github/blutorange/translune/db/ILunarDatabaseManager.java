package com.github.blutorange.translune.db;

import org.eclipse.jdt.annotation.Nullable;

public interface ILunarDatabaseManager {

	public static enum ELunarDatabaseManagerMock implements ILunarDatabaseManager {
		INSTANCE;

		@Override
		public <T> T find(final Class<T> clazz, final Object primaryKey) {
			return null;
		}
	}

	@Nullable
	public <T> T find(final Class<T> clazz, final Object primaryKey);
}
