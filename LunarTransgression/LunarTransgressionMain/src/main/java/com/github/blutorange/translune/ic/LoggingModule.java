package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.blutorange.translune.CustomProperties;
import com.github.blutorange.translune.db.LunarDatabaseManager;
import com.github.blutorange.translune.gui.AbstractBean;
import com.github.blutorange.translune.gui.PlayerBean;
import com.github.blutorange.translune.socket.LunarDecoder;
import com.github.blutorange.translune.socket.LunarEncoder;
import com.github.blutorange.translune.socket.LunarEndpoint;
import com.github.blutorange.translune.socket.LunarMessage;
import com.github.blutorange.translune.socket.TestEndpoint;

import dagger.Module;
import dagger.Provides;

@Module
public class LoggingModule {
	@Provides @Singleton @Classed(LunarMessage.class) static Logger provideLogger1() {
		return provideLogger(LunarMessage.class);
	}

	@Provides @Singleton @Classed(LunarDatabaseManager.class) static Logger provideLogger3() {
		return provideLogger(LunarDatabaseManager.class);
	}

	@Provides @Singleton @Classed(LunarEncoder.class) static Logger provideLogger4() {
		return provideLogger(LunarEncoder.class);
	}

	@Provides @Singleton @Classed(LunarDecoder.class) static Logger provideLogger5() {
		return provideLogger(LunarDecoder.class);
	}

	@Provides @Singleton @Classed(LunarEndpoint.class) static Logger provideLogger6() {
		return provideLogger(LunarEndpoint.class);
	}

	@Provides @Singleton @Classed(CustomProperties.class) static Logger provideLogger7() {
		return provideLogger(CustomProperties.class);
	}

	@Provides @Singleton @Classed(TestEndpoint.class) static Logger provideLogger8() {
		return provideLogger(TestEndpoint.class);
	}

	@Provides @Singleton @Classed(DatabaseModule.class) static Logger provideLogger9() {
		return provideLogger(DatabaseModule.class);
	}

	@Provides @Singleton @Classed(AbstractBean.class) static Logger provideLogger10() {
		return provideLogger(AbstractBean.class);
	}

	@Provides @Singleton @Classed(PlayerBean.class) static Logger provideLogger11() {
		return provideLogger(PlayerBean.class);
	}

	private static Logger provideLogger(final Class<?> clazz) {
		return LoggerFactory.getLogger(clazz);
	}
}
