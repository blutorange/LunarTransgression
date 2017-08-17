package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import com.github.blutorange.translune.logic.BattleProcessing;
import com.github.blutorange.translune.logic.BattleRunner;
import com.github.blutorange.translune.logic.IBattleProcessing;
import com.github.blutorange.translune.logic.IBattleRunner;

import dagger.Component;

@Singleton
@Component(modules={LogicModule.class, LoggingModule.class, SocketModule.class, StorageModule.class, DatabaseModule.class})
public interface LogicComponent {
	void inject(BattleRunner battle);
	void inject(BattleProcessing battle);
	IBattleRunner battleRunner();
	IBattleProcessing battleProcessing();
}