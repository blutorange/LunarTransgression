package com.github.blutorange.translune.logic;

import java.util.Map;

import com.github.blutorange.translune.db.EStatusValue;

public interface IStaged {
	Map<EStatusValue, Integer> getStageChanges();
	int getStageChance();
}