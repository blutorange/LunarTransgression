package com.github.blutorange.translune.i18n;

import java.util.Locale;

public interface ILocalizationBundle {
	/**
	 * @param locale
	 * @return Provider for the requested locale, or the best alternative.
	 */
	ILocalizationProvider getFor(Locale locale);
	/**
	 * @param locale
	 * @return Whether a provider matching the requested locale exactly exists.
	 */
	boolean canProvide(Locale locale);
	/**
	 * @return The default provider.
	 */
	ILocalizationProvider getDefault();
	/**
	 *
	 * @param languageTag
	 * @return
	 * @see #getFor(Locale)
	 */
	default ILocalizationProvider getFor(final String languageTag) {
		return getFor(Locale.forLanguageTag(languageTag));
	}
}