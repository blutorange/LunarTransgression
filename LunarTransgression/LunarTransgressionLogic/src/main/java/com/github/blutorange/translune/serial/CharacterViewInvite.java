package com.github.blutorange.translune.serial;

import org.apache.commons.lang3.StringUtils;

import com.github.blutorange.translune.db.CharacterState;
import com.jsoniter.annotation.JsonIgnore;

public class CharacterViewInvite {
	private final int level;
	private final String imgIcon;

	@Deprecated
	public CharacterViewInvite() {
		level = 1;
		imgIcon = StringUtils.EMPTY;
	}

	public CharacterViewInvite(final CharacterState characterState) {
		this.level = characterState.getLevel();
		this.imgIcon = characterState.getCharacter().getImgIcon();
	}

	/**
	 * @return the level
	 */
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public int getLevel() {
		return level;
	}
	/**
	 * @return the imgIcon
	 */
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public String getImgIcon() {
		return imgIcon;
	}
}