package com.github.blutorange.translune.i18n;

public interface ILocalizationProvider {
	String get(String key);
	String get(String key, Object... arguments);
}
