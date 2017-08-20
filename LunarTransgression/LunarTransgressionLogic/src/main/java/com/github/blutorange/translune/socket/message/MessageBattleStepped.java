package com.github.blutorange.translune.socket.message;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.logic.BattleAction;
import com.github.blutorange.translune.logic.IBattleAction;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;
import com.jsoniter.annotation.JsonProperty;

public class MessageBattleStepped implements ILunarPayload {
	@JsonProperty(required = true, implementation = BattleAction.class)
	private IBattleAction[] battleResults;

	@Deprecated
	public MessageBattleStepped() {
		battleResults = new IBattleAction[0];
	}

	public MessageBattleStepped(final IBattleAction[] battleResults) {
		this.battleResults = battleResults;
	}

	/**
	 * @return the battleResults
	 */
	public IBattleAction[] getBattleResults() {
		return battleResults;
	}

	/**
	 * @param battleResults
	 *            the battleResults to set
	 */
	public void setBattleResults(final IBattleAction@Nullable[] battleResults) {
		this.battleResults = battleResults != null ? battleResults : new IBattleAction[0];
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.BATTLE_STEPPED;
	}
}