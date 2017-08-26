package com.github.blutorange.translune.ic;

public class ComponentFactory {
	private static final LunarComponent lunarComponent = DaggerLunarComponent.create();

	public static LunarComponent getLunarComponent() {
		return lunarComponent;
	}
}