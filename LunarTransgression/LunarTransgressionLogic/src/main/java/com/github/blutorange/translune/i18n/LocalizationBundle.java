package com.github.blutorange.translune.i18n;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.jdt.annotation.NonNull;

@Singleton
public final class LocalizationBundle implements ILocalizationBundle {

	protected static class LocalizationProvider implements ILocalizationProvider {
		private final ResourceBundle rb;

		public LocalizationProvider(final ResourceBundle rb) {
			this.rb = rb;
		}

		@Override
		public String get(final String key) {
			final String value = rb.getString(key);
			if (value == null)
				return String.format("???%s???", key);
			return value;
		}

		@Override
		public String get(final String key, final Object... arguments) {
			final String value = rb.getString(key);
			if (value == null)
				return String.format("???%s???", key);
			return MessageFormat.format(value, arguments);
		}
	}

	private static final String BUNDLE_BASE_NAME = "i18n";

	@SuppressWarnings("null")
	private static @NonNull Locale[] SUPPORTED_LANGS = new Locale[] { Locale.ENGLISH };

	private final ILocalizationProvider defaultProvider;

	private final Map<Locale, ILocalizationProvider> map = new HashMap<>();

	@Inject
	public LocalizationBundle() {
		for (final Locale locale : SUPPORTED_LANGS)
			loadLang(locale);
		final ILocalizationProvider defaultProvider = map.get(Locale.ENGLISH);
		if (defaultProvider == null)
			throw new IllegalStateException("Failed to load English as the fallback locale.");
		this.defaultProvider = defaultProvider;
	}

	@Override
	public boolean canProvide(final Locale locale) {
		return map.containsKey(locale);
	}

	@Override
	public ILocalizationProvider getDefault() {
		return defaultProvider;
	}

	@Override
	public ILocalizationProvider getFor(final Locale locale) {
		final ILocalizationProvider p = map.get(locale);
		if (p != null)
			return p;
		return defaultProvider;
	}

	private void loadLang(final Locale locale) {
		final ResourceBundle rb = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale,
				LocalizationBundle.class.getClassLoader());
		if (rb == null)
			return;
		map.put(locale, new LocalizationProvider(rb));
	}
}