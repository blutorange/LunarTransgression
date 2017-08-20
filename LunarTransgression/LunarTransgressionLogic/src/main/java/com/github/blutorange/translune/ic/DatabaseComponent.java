package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.LunarDatabaseManager;

import dagger.Component;

@Singleton
@Component(modules={DatabaseModule.class, LoggingModule.class, LogicModule.class})
public interface DatabaseComponent {
	void inject(LunarDatabaseManager lunarDatabaseManager);
	ILunarDatabaseManager iLunarDatabaseManager();
}
