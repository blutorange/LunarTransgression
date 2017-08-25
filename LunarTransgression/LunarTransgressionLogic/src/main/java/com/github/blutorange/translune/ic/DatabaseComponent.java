package com.github.blutorange.translune.ic;

import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.LunarDatabaseManager;
import com.github.blutorange.translune.serial.IImportProcessing;
import com.github.blutorange.translune.serial.ImportProcessing;

import dagger.Component;

@Singleton
@Component(modules={DatabaseModule.class, LoggingModule.class, LogicModule.class})
public interface DatabaseComponent {
	void inject(LunarDatabaseManager lunarDatabaseManager);
	ILunarDatabaseManager iLunarDatabaseManager();
	EntityManagerFactory entityManagerFactory();
	ImportProcessing importProcessing();
	IImportProcessing iImportProcessing();
}
