package com.github.blutorange.translune.socket.message;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.logic.IBattleResult;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarMessage;
import com.jsoniter.annotation.JsonProperty;

public class MessageBattleStepped implements ILunarMessage {
	@JsonProperty(required = true)
	private IBattleResult[] battleResults;

	@Deprecated
	public MessageBattleStepped() {
		battleResults = new IBattleResult[0];
	}

	public MessageBattleStepped(final IBattleResult[] battleResults) {
		this.battleResults = battleResults;
	}

	/**
	 * @return the battleResults
	 */
	public IBattleResult[] getBattleResults() {
		return battleResults;
	}

	/**
	 * @param battleResults
	 *            the battleResults to set
	 */
	public void setBattleResults(final IBattleResult@Nullable[] battleResults) {
		this.battleResults = battleResults != null ? battleResults : new IBattleResult[0];
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.BATTLE_STEPPED;
	}
}