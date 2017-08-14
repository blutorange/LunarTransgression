package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import com.github.blutorange.translune.logic.Battle;

import dagger.Component;

@Singleton
@Component(modules={StorageModule.class})
public interface LogicComponent {
	void inject(Battle battle);
	Battle battle();
}
