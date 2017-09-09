package com.github.blutorange.translune.serial;

import org.apache.commons.lang3.StringUtils;

import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.util.Constants;
import com.jsoniter.annotation.JsonIgnore;

public class PlayerViewInvite {
	private final String nickname;
	private final String description;
	private final String imgAvatar;
	private final CharacterViewInvite[] characterStates;

	@Deprecated
	public PlayerViewInvite() {
		nickname = StringUtils.EMPTY;
		description = StringUtils.EMPTY;
		imgAvatar = Constants.DEFAULT_PLAYER_AVATAR;
		characterStates = new CharacterViewInvite[0];
	}

	public PlayerViewInvite(final Player player) {
		this.nickname = player.getNickname();
		this.description = player.getDescription();
		this.imgAvatar = player.getImgAvatar();
		this.characterStates = player.getUnmodifiableCharacterStates().stream().map(CharacterViewInvite::new)
				.toArray(CharacterViewInvite[]::new);
	}

	/**
	 * @return the nickname
	 */
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public String getNickname() {
		return nickname;
	}

	/**
	 * @return the nickname
	 */
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public String getImgAvatar() {
		return imgAvatar;
	}

	/**
	 * @return the description
	 */
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public String getDescription() {
		return description;
	}

	/**
	 * @return the characterStates
	 */
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public CharacterViewInvite[] getCharacterStates() {
		return characterStates;
	}
}