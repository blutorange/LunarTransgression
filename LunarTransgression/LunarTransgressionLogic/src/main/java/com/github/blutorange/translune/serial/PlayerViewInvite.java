package com.github.blutorange.translune.serial;

import org.apache.commons.lang3.StringUtils;

import com.github.blutorange.translune.db.Player;
import com.jsoniter.annotation.JsonIgnore;

public class PlayerViewInvite {
	private final String nickname;
	private final String description;
	private final CharacterViewInvite[] characterStates;

	@Deprecated
	public PlayerViewInvite() {
		nickname = StringUtils.EMPTY;
		description = StringUtils.EMPTY;
		characterStates = new CharacterViewInvite[0];
	}

	public PlayerViewInvite(final Player player) {
		this.nickname = player.getNickname();
		this.description = player.getDescription();
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