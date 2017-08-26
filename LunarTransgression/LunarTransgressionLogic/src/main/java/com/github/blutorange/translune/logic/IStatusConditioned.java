package com.github.blutorange.translune.logic;

import org.eclipse.jdt.annotation.Nullable;

public interface IStatusConditioned {
	@Nullable EStatusCondition getCondition();
	int getConditionChance();
}
