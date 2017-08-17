package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import com.github.blutorange.translune.logic.BattleRunnable;

import dagger.Component;

@Singleton
@Component(modules={StorageModule.class})
public interface LogicComponent {
	void inject(BattleRunnable battle);
	BattleRunnable battle();
}
