package com.github.blutorange.translune;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.util.Constants;

@Singleton
public class CustomProperties {
	@Inject @Classed(CustomProperties.class) Logger logger;

	private final Properties properties = new Properties();

	private long timeoutInitIdMillis = -1L;

	private final long timeoutMessageQueueMillis;

	private final int maxThreadCount;

	private final int battlePreparationTimeoutMillis;

	private final int battleRoundTimeoutMillis;

	@Inject
	public CustomProperties() {
		try (InputStream is = CustomProperties.class.getClassLoader().getResourceAsStream("custom.properties")) {
			properties.load(is);
		}
		catch (final IOException e) {
			logger.error("failed to load custom properties", e);
		}
		long timeoutInitIdMillis = Long.parseLong(get(Constants.CUSTOM_KEY_TIMEOUT_INITID_MILLIS, "60000"));
		if (timeoutInitIdMillis < 1000L) timeoutInitIdMillis = 1000L;
		this.timeoutInitIdMillis = timeoutInitIdMillis;

		long timeoutMessageQueueMillis = Long.parseLong(get(Constants.CUSTOM_KEY_TIMEOUT_MESSAGEQUEUE_MILLIS, "60000"));
		if (timeoutMessageQueueMillis < 1000L) timeoutMessageQueueMillis = 1000L;
		this.timeoutMessageQueueMillis = timeoutMessageQueueMillis;

		int maxThreadCount = Integer.parseInt(get(Constants.CUSTOM_KEY_MAX_THREAD_COUNT, "100"));
		if (maxThreadCount < 1) maxThreadCount = 1;
		this.maxThreadCount = maxThreadCount;

		int battlePreparationTimeoutMillis = Integer.parseInt(get(Constants.CUSTOM_KEY_BATTLE_PREPARATION_TIMEOUT, "30000"));
		if (battlePreparationTimeoutMillis < 1000) battlePreparationTimeoutMillis = 1000;
		this.battlePreparationTimeoutMillis = battlePreparationTimeoutMillis;

		int battleRoundTimeoutMillis = Integer.parseInt(get(Constants.CUSTOM_KEY_BATTLE_ROUND_TIMEOUT, "60000"));
		if (battleRoundTimeoutMillis < 1000) battleRoundTimeoutMillis = 1000;
		this.battleRoundTimeoutMillis = battleRoundTimeoutMillis;
	}

	@Nullable
	public String get(final String key) {
		return properties.getProperty(key);
	}

	public String get(final String key, final String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public String getSadminPass() {
		return get(Constants.CUSTOM_KEY_SADMIN_PASS, "sadmin");
	}

	public long getTimeoutInitIdMillis() {
		return timeoutInitIdMillis;
	}

	public long getTimeoutMessageQueueMillis() {
		return timeoutMessageQueueMillis;
	}

	public int getMaxThreadCount() {
		return maxThreadCount;
	}

	public int getBattlePreparationTimeoutMillis() {
		return battlePreparationTimeoutMillis;
	}
	
	public int getBattleStepTimeoutMillis() {
		return battleRoundTimeoutMillis;
	}
}