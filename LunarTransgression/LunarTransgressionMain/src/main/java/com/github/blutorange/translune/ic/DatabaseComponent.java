package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import com.github.blutorange.translune.db.LunarDatabaseManager;
import com.github.blutorange.translune.gui.AdminBean;
import com.github.blutorange.translune.gui.SessionBean;

import dagger.Component;

@Singleton
@Component(modules={DatabaseModule.class, LoggingModule.class})
public interface DatabaseComponent {
	void inject(LunarDatabaseManager lunarDatabaseManager);
	void inject(SessionBean sessionBean);
	void inject(AdminBean sessionBean);
}
