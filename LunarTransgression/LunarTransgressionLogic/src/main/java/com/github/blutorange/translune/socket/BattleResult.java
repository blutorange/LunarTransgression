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
	 * Sentences to display, eg. "XX received 10 EXP", "XX grew to level 11!",
	 * "XX learned Sand Attack!"
	 */
	@JsonProperty(required = true)
	private final String[] sentences;

	@Deprecated
	public BattleResult() {
		character = StringUtils.EMPTY;
		sentences = ArrayUtils.EMPTY_STRING_ARRAY;
	}

	public BattleResult(final String character, final String... sentences) {
		this.character = character;
		this.sentences = sentences;
	}

	/**
	 * @return the character
	 */
	public String getCharacter() {
		return character;
	}

	

	/**
	 * @return the sentences
	 */
	public String[] getSentences() {
		return sentences;
	}	
}