package com.github.blutorange.translune.message;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;
import com.jsoniter.annotation.JsonProperty;

public class MessageBattleStepped implements ILunarPayload {
	@JsonProperty(required = true, implementation = BattleAction.class)
	private BattleAction[] battleResults;

	@Deprecated
	public MessageBattleStepped() {
		battleResults = new BattleAction[0];
	}

	public MessageBattleStepped(final BattleAction[] battleResults) {
		this.battleResults = battleResults;
	}

	/**
	 * @return the battleResults
	 */
	public BattleAction[] getBattleResults() {
		return battleResults;
	}

	/**
	 * @param battleResults
	 *            the battleResults to set
	 */
	public void setBattleResults(final BattleAction@Nullable[] battleResults) {
		this.battleResults = battleResults != null ? battleResults : new BattleAction[0];
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.BATTLE_STEPPED;
	}
}