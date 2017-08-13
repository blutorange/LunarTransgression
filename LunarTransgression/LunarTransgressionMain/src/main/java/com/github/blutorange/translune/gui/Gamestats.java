package com.github.blutorange.translune.gui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.primefaces.model.chart.MeterGaugeChartModel;

public class Gamestats {
	private final MeterGaugeChartModel playerMeter;
	private final MeterGaugeChartModel characterMeter;
	private final MeterGaugeChartModel itemMeter;

	public Gamestats() {
		this(0L, 0L, 0L);
	}

	public Gamestats(@Nullable final Long playerCount, @Nullable final Long characterCount, @Nullable final Long itemCount) {
		long base = playerCount != null ? playerCount.longValue() : 100;
		base = 10 * (int)(1+Math.log10(base));
		playerMeter = createGauge("Players", "people", base, playerCount);
		characterMeter = createGauge("Characters", "chars", base*20, characterCount);
		itemMeter = createGauge("Items", "items", base*30, itemCount);
	}

	public MeterGaugeChartModel getPlayerMeter() {
		return playerMeter;
	}

	public MeterGaugeChartModel getItemMeter() {
		return itemMeter;
	}

	public MeterGaugeChartModel getCharacterMeter() {
		return characterMeter;
	}

	private MeterGaugeChartModel createGauge(final String name, final String unit, final long max, @Nullable final Long value) {
		final long v = value != null ? value.longValue() : 0;
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
