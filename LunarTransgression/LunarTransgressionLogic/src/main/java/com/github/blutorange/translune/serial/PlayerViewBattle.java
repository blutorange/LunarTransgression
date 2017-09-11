package com.github.blutorange.translune.serial;

import org.apache.commons.lang3.StringUtils;

import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.logic.IComputedBattleStatus;
import com.github.blutorange.translune.util.Constants;
import com.jsoniter.annotation.JsonIgnore;

public class PlayerViewBattle {
	private final String nickname;
	private final String imgAvatar;
	private final CharacterViewBattle[] characterStates;
	private final IComputedBattleStatus[] battleStatus;

	@Deprecated
	public PlayerViewBattle() {
		nickname = StringUtils.EMPTY;
		imgAvatar = Constants.DEFAULT_PLAYER_AVATAR;
		characterStates = new CharacterViewBattle[0];
		battleStatus = new IComputedBattleStatus[0];
	}

	public PlayerViewBattle(final Player player, final IComputedBattleStatus[] battleStatus) {
		this.nickname = player.getNickname();
		this.imgAvatar = player.getImgAvatar();
		this.battleStatus = new IComputedBattleStatus[battleStatus.length];
		this.characterStates = new CharacterViewBattle[battleStatus.length];
		for (int i = 0; i < battleStatus.length; ++i) {
			this.battleStatus[i] = battleStatus[i].getSnapshot();
			this.characterStates[i] = new CharacterViewBattle(battleStatus[i].getCharacterState());
		}
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
	 * @return the characterStates
	 */
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public CharacterViewBattle[] getCharacterStates() {
		return characterStates;
	}

	/**
	 * @return the battleStatus
	 */
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public IComputedBattleStatus[] getBattleStatus() {
		return battleStatus;
	}
}