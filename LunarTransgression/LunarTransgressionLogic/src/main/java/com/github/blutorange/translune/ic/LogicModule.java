package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import com.github.blutorange.translune.logic.BattleProcessing;
import com.github.blutorange.translune.logic.BattleRunner;
import com.github.blutorange.translune.logic.IBattleProcessing;
import com.github.blutorange.translune.logic.IBattleRunner;

import dagger.Module;
import dagger.Provides;

@Module
public class LogicModule {
	@Provides @Singleton
	static IBattleProcessing provideBattleProcessing() {
		return new BattleProcessing();
	}

	@Provides @Singleton
	static IBattleRunner provideBattleRunner() {
		return new BattleRunner();
	}
}