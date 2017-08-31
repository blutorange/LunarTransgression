package com.github.blutorange.translune.ic;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Random;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Named;
import javax.inject.Singleton;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import com.github.blutorange.translune.Sandbox;
import com.github.blutorange.translune.i18n.ILocalizationBundle;
import com.github.blutorange.translune.logic.BattleProcessing;
import com.github.blutorange.translune.logic.BattleRunner;
import com.github.blutorange.translune.logic.IBattleProcessing;
import com.github.blutorange.translune.logic.IBattleRunner;
import com.github.blutorange.translune.logic.IRandomSupplier;
import com.github.blutorange.translune.media.IImageProcessing;

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
		return ComponentFactory.getLunarComponent()._localizationBundle();
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

	@Provides
	@Named("secure")
	IRandomSupplier provideRandomSecure() {
		final ThreadLocal<Random> threadLocal = ThreadLocal.withInitial(() -> new SecureRandom());
		return () -> threadLocal.get();
	}

	@Provides
	@Named("basic")
	IRandomSupplier provideRandomBasic() {
		final ThreadLocal<Random> threadLocal = ThreadLocal.withInitial(() -> new Random());
		return () -> threadLocal.get();
	}

	@Provides
	@Singleton
	IImageProcessing provideImageProcessing() {
		return ComponentFactory.getLunarComponent()._imageProcessing();
	}

	@Provides
	@Singleton
	MimetypesFileTypeMap provideMimetypesFileTypeMap() {
		try (InputStream is = Sandbox.class.getClassLoader().getResourceAsStream("mime.types")) {
			return new MimetypesFileTypeMap(is);
		}
		catch (final IOException e) {
			throw new RuntimeException("did not find mime.types or it was invalid", e);
		}
	}

}