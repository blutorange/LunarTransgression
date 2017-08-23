package com.github.blutorange.translune.logic;

import com.github.blutorange.common.IntToIntFunction;

public enum EExperienceGroup {
	ERRATIC( n ->
		n <= 50 ? (n*n*n*(100-n)/50) : 
			n <= 68 ? (n*n*n*(150-n)/100) :
				n <= 98 ? ((n*n*n*((1911-10*n)/3))/500) :
					(n*n*n*(160-n)/100)				
	),
	FLUCTUATING( n ->
		n <= 15 ? (n*n*n*((n+1)/3+24)/50) :
			n <= 36 ? (n*n*n*(n+14)/50) :
				(n*n*n*(n/2+32)/50)
	),
	FAST(n -> 4*n*n*n/5),
	MEDIUM_FAST(n -> n*n*n),
	MEDIUM_SLOW(n -> (n*(n*(6*n-75)+500)-700)/5),
	SLOW(n -> 5*n*n*n/4),
	;
	
	private int[] cumulated;
	private IntToIntFunction expCurve;

	private EExperienceGroup(IntToIntFunction expCurve) {
		this.expCurve = expCurve;
		cumulated = new int[100];
		for (int level = 101; level --> 1 ;) {
			cumulated[level-1] = expCurve.apply(level);
		}
	}
	
	public int getExperienceForLevel(int level) {
		if (level <= 1)
			return 0;
		if (level >= 100)
			return cumulated[99];
		return cumulated[level-1];
	}
	
}
