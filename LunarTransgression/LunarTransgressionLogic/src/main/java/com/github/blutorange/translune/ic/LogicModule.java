package com.github.blutorange.translune.ic;

import java.security.SecureRandom;
import java.util.Random;

import javax.inject.Named;
import javax.inject.Singleton;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import com.github.blutorange.translune.i18n.ILocalizationBundle;
import com.github.blutorange.translune.logic.BattleProcessing;
import com.github.blutorange.translune.logic.BattleRunner;
import com.github.blutorange.translune.logic.IBattleProcessing;
import com.github.blutorange.translune.logic.IBattleRunner;

import dagger.Module;
import dagger.Provides;

@Module
public class LogicModule {
	@Provides
	@Singleton
	static IBattleProcessing provideBattleProcessing() {
		return new BattleProcessing();
	}

	@Provides
	@Singleton
	static IBattleRunner provideBattleRunner() {
		return new BattleRunner();
	}

	@Provides
	@Singleton
	static SchedulerFactory provideSchedulerFactory() {
		final SchedulerFactory sf = new StdSchedulerFactory();
		return sf;
	}

	@Provides
	@Singleton
	static ILocalizationBundle provideLocalizationBundle() {
		return ComponentFactory.getLogicComponent().localizationBundle();
	}

	@Provides
	@Named("default")
	@Singleton
	Scheduler provideSchedulerDefault(@Classed(LogicModule.class) final Logger logger,
			final SchedulerFactory schedulerFactory) {
		Scheduler scheduler;
		try {
			scheduler = schedulerFactory.getScheduler();
		}
		catch (final SchedulerException e) {
			logger.error("failed to create scheduler", e);
			throw new RuntimeException("failed to create scheduler");
		}
		return scheduler;
	}

	@Provides @Named("secure")
	Random provideRandomSecure() {
		return new SecureRandom();
	}

	@Provides @Named("basic")
	Random provideRandomBasic() {
		return new Random();
	}
}