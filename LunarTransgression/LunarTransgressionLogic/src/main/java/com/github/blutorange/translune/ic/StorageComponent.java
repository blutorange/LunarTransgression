package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import com.github.blutorange.translune.logic.BattleStore;

import dagger.Component;

@Singleton
@Component(modules={StorageModule.class, LoggingModule.class})
public interface StorageComponent {
	void inject(BattleStore battleStore);
	BattleStore battleStore();	
}