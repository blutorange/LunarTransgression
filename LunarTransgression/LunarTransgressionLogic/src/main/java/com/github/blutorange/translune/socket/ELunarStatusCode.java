package com.github.blutorange.translune.socket;

public enum ELunarStatusCode {
	OK(0),
	GENERIC_ERROR(20),
	ACCESS_DENIED(21),
	;

	public final int numerical;
	private static ELunarStatusCode[] map = new ELunarStatusCode[30];

	static {
		for (final ELunarStatusCode code : ELunarStatusCode.values())
			map[code.numerical] = code;
	}

	private ELunarStatusCode(final int numerical) {
		this.numerical = numerical;
	}

	public static ELunarStatusCode valueOf(final int statusCode) {
		if (statusCode < 0 || statusCode >= 30)
			throw new IllegalArgumentException("no such enum with status code " + statusCode);
		final ELunarStatusCode status = map[statusCode];
		if (status == null)
			throw new IllegalArgumentException("no such enum with status code " + statusCode);
		return status;
	}
}
