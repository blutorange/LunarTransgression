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
}