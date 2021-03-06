package com.github.blutorange.translune.message;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.logic.IComputedBattleStatus;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;
import com.jsoniter.annotation.JsonProperty;

public class MessageBattleStepped implements ILunarPayload {
	private BattleAction[] battleResults;

	private final IComputedBattleStatus[] player;
	private final IComputedBattleStatus[] opponent;

	/**
	 * 0 iff the battle is not over. 1 iff the player won. -1 iff the player
	 * lost.
	 */
	@JsonProperty(required = true)
	private int causesEnd;

	@Deprecated
	public MessageBattleStepped() {
		battleResults = new BattleAction[0];
		player = new IComputedBattleStatus[0];
		opponent = new IComputedBattleStatus[0];
	}

	public MessageBattleStepped(final BattleAction[] battleResults, final IComputedBattleStatus[] player,
			final IComputedBattleStatus[] opponent, final int causesEnd) {
		this.battleResults = battleResults;
		this.causesEnd = causesEnd;
		this.player = player;
		this.opponent = opponent;
	}

	/**
	 * @return the battleResults
	 */
	@JsonProperty(required = true)
	public BattleAction[] getBattleResults() {
		return battleResults;
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.BATTLE_STEPPED;
	}

	/**
	 * @return the player
	 */
	@JsonProperty(required = true)
	public IComputedBattleStatus[] getPlayer() {
		return player;
	}

	/**
	 * @return the opponent
	 */
	@JsonProperty(required = true)
	public IComputedBattleStatus[] getOpponent() {
		return opponent;
	}

	/**
	 * @return the winLoseState
	 */
	public int getCausesEnd() {
		return causesEnd;
	}

	/**
	 * @param battleResults
	 *            the battleResults to set
	 */
	public void setBattleResults(final BattleAction @Nullable [] battleResults) {
		this.battleResults = battleResults != null ? battleResults : new BattleAction[0];
	}

	/**
	 * @param causesEnd
	 *            the winLoseState to set
	 */
	public void setCausesEnd(final int causesEnd) {
		this.causesEnd = causesEnd;
	}
}