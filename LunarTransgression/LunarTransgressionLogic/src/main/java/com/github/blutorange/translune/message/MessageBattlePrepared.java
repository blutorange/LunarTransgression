package com.github.blutorange.translune.message;

import com.github.blutorange.translune.serial.PlayerViewBattle;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;
import com.jsoniter.annotation.JsonIgnore;

public class MessageBattlePrepared implements ILunarPayload {

	private final PlayerViewBattle player;
	private final PlayerViewBattle opponent;

	@Deprecated
	public MessageBattlePrepared() {
		player = new PlayerViewBattle();
		opponent = new PlayerViewBattle();
	}

	public MessageBattlePrepared(final PlayerViewBattle player, final PlayerViewBattle opponent) {
		this.player = player;
		this.opponent = opponent;
	}

	/**
	 * @return the player
	 */
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public PlayerViewBattle getPlayer() {
		return player;
	}

	/**
	 * @return the opponent
	 */
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public PlayerViewBattle getOpponent() {
		return opponent;
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.BATTLE_PREPARED;
	}
}