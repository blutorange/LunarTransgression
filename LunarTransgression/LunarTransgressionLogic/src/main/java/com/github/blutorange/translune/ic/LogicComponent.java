package com.github.blutorange.translune.ic;

import java.util.Random;

import javax.inject.Named;
import javax.inject.Singleton;

import org.quartz.Scheduler;

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
	@Named("default") Scheduler defaultScheduler();
	@Named("secure") Random randomSecure();
	@Named("basic") Random randomBasic();
}