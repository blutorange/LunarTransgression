package com.github.blutorange.translune.serial;

import org.apache.commons.lang3.StringUtils;

import com.github.blutorange.translune.db.CharacterState;
import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;

public class CharacterStateViewLoot {
	private final int level;
	private final String imgIcon;
	private final String id;

	@Deprecated
	public CharacterStateViewLoot() {
		level = 1;
		imgIcon = StringUtils.EMPTY;
		id = StringUtils.EMPTY;
	}

	public CharacterStateViewLoot(final CharacterState characterState) {
		this.level = characterState.getLevel();
		this.imgIcon = characterState.getCharacter().getImgIcon();
		this.id = characterState.getId();
	}

	/**
	 * @return the level
	 */
	@JsonProperty(required = true)
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public int getLevel() {
		return level;
	}
	/**
	 * @return the imgIcon
	 */
	@JsonProperty(required = true)
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public String getImgIcon() {
		return imgIcon;
	}

	@JsonProperty(required = true)
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public String getId() {
		return id;
	}
}