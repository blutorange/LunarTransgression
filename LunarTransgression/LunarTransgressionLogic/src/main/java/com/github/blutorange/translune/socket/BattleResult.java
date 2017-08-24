package com.github.blutorange.translune.socket;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.jsoniter.annotation.JsonProperty;

/**
 * A battle result with experience gain and leveling up.
 *
 * @author madgaksha
 */
public class BattleResult {
	@JsonProperty(required = true)
	private final String character;

	/**
	 * How many level the character gained.
	 */
	@JsonProperty(required = true)
	private final int levels;

	/**
	 * Sentences to display, eg. "XX received 10 EXP", "XX grew to level 11!",
	 * "XX learned Sand Attack!"
	 */
	@JsonProperty(required = true)
	private final String[] sentences;

	@Deprecated
	public BattleResult() {
		character = StringUtils.EMPTY;
		sentences = ArrayUtils.EMPTY_STRING_ARRAY;
		levels = 0;
	}

	public BattleResult(final String character, final int levels, final String... sentences) {
		this.character = character;
		this.levels = levels;
		this.sentences = sentences;
	}
}