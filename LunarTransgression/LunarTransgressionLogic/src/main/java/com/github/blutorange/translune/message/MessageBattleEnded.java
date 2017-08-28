package com.github.blutorange.translune.message;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.BattleResult;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;
import com.jsoniter.annotation.JsonProperty;

public class MessageBattleEnded implements ILunarPayload {
	private BattleResult[] battleResults;
	boolean isVictory;

	@Deprecated
	public MessageBattleEnded() {
		battleResults = new BattleResult[0];
	}

	public MessageBattleEnded(final boolean isVictory, final BattleResult[] battleResults) {
		this.isVictory = isVictory;
		this.battleResults = battleResults;
	}

	/**
	 * @return the battleResults
	 */
	public BattleResult[] getBattleResults() {
		return battleResults;
	}

	/**
	 * @return Whether the player won.
	 */
	@JsonProperty(required = true)
	public boolean getIsVictory() {
		return isVictory;
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.BATTLE_ENDED;
	}

	/**
	 * @param battleResults
	 *            the battleResults to set
	 */
	public void setBattleResults(final BattleResult @Nullable [] battleResults) {
		this.battleResults = battleResults != null ? battleResults : new BattleResult[0];
	}

	/**
	 * @param isVictory
	 *            Whether the player won.
	 */
	public void setVictory(final boolean isVictory) {
		this.isVictory = isVictory;
	}
}