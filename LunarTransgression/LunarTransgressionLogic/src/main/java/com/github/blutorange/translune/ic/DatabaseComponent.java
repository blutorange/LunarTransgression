package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import com.github.blutorange.translune.db.LunarDatabaseManager;

import dagger.Component;

@Singleton
@Component(modules={DatabaseModule.class, LoggingModule.class})
public interface DatabaseComponent {
	void inject(LunarDatabaseManager lunarDatabaseManager);
	LunarDatabaseManager lunarDatabaseManager();
}
