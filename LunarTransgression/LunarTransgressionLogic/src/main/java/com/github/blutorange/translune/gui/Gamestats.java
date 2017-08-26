package com.github.blutorange.translune.gui;

import java.util.ArrayList;
import java.util.List;

import org.primefaces.model.chart.MeterGaugeChartModel;

public class Gamestats {
	private final MeterGaugeChartModel playerMeter;
	private final MeterGaugeChartModel characterMeter;

	public Gamestats() {
		this(0L, 0L);
	}

	public Gamestats(final long playerCount, final long characterCount) {
		long base = playerCount;
		base = 10 * (int)(1+Math.log10(base));
		playerMeter = createGauge("Players", "people", base, playerCount);
		characterMeter = createGauge("Characters", "chars", base*20, characterCount);
	}

	public MeterGaugeChartModel getPlayerMeter() {
		return playerMeter;
	}

	public MeterGaugeChartModel getCharacterMeter() {
		return characterMeter;
	}

	private MeterGaugeChartModel createGauge(final String name, final String unit, final long max, final long v) {
		final MeterGaugeChartModel gauge = new MeterGaugeChartModel();
		final long n = 10+1;
		final List<Number> intervals = new ArrayList<>((int)n);
		for (int i = 0; i <= n; ++i)
			intervals.add(Long.valueOf(i*max/n));
		gauge.setTitle(name);
		gauge.setGaugeLabel(unit);
		gauge.setIntervals(intervals);
		gauge.setMin(0);
		gauge.setMax(max);
		gauge.setValue(Long.valueOf(v));
		return gauge;
	}
}