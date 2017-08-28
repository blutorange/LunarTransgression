package com.github.blutorange.translune.message;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.BattleCommand;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;
import com.jsoniter.annotation.JsonProperty;

public class MessageStepBattle implements ILunarPayload {
	private final static BattleCommand COMMAND_DEFENSE = new BattleCommand();

	private BattleCommand battleCommandCharacter1 = COMMAND_DEFENSE;
	private BattleCommand battleCommandCharacter2 = COMMAND_DEFENSE;
	private BattleCommand battleCommandCharacter3 = COMMAND_DEFENSE;
	private BattleCommand battleCommandCharacter4 = COMMAND_DEFENSE;

	@Deprecated
	public MessageStepBattle() {
	}

	public MessageStepBattle(final BattleCommand battleCommandCharacter1, final BattleCommand battleCommandCharacter2,
			final BattleCommand battleCommandCharacter3, final BattleCommand battleCommandCharacter4) {
		this.battleCommandCharacter1 = battleCommandCharacter1;
		this.battleCommandCharacter2 = battleCommandCharacter2;
		this.battleCommandCharacter3 = battleCommandCharacter3;
		this.battleCommandCharacter4 = battleCommandCharacter4;
	}

	/**
	 * @return the battleCommandCharacter1
	 */
	@JsonProperty(required = true)
	public BattleCommand getBattleCommandCharacter1() {
		return battleCommandCharacter1;
	}

	/**
	 * @return the battleCommandCharacter2
	 */
	@JsonProperty(required = true)
	public BattleCommand getBattleCommandCharacter2() {
		return battleCommandCharacter2;
	}

	/**
	 * @return the battleCommandCharacter3
	 */
	@JsonProperty(required = true)
	public BattleCommand getBattleCommandCharacter3() {
		return battleCommandCharacter3;
	}

	/**
	 * @return the battleCommandCharacter4
	 */
	@JsonProperty(required = true)
	public BattleCommand getBattleCommandCharacter4() {
		return battleCommandCharacter4;
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.STEP_BATTLE;
	}

	/**
	 * @param battleCommandCharacter1
	 *            the battleCommandCharacter1 to set
	 */
	public void setBattleCommandCharacter1(@Nullable final BattleCommand battleCommandCharacter1) {
		this.battleCommandCharacter1 = battleCommandCharacter1 != null ? battleCommandCharacter1 : COMMAND_DEFENSE;
	}

	/**
	 * @param battleCommandCharacter2
	 *            the battleCommandCharacter2 to set
	 */
	public void setBattleCommandCharacter2(@Nullable final BattleCommand battleCommandCharacter2) {
		this.battleCommandCharacter2 = battleCommandCharacter2 != null ? battleCommandCharacter2 : COMMAND_DEFENSE;
	}

	/**
	 * @param battleCommandCharacter3
	 *            the battleCommandCharacter3 to set
	 */
	public void setBattleCommandCharacter3(@Nullable final BattleCommand battleCommandCharacter3) {
		this.battleCommandCharacter3 = battleCommandCharacter3 != null ? battleCommandCharacter3 : COMMAND_DEFENSE;
	}

	/**
	 * @param battleCommandCharacter4
	 *            the battleCommandCharacter4 to set
	 */
	public void setBattleCommandCharacter4(@Nullable final BattleCommand battleCommandCharacter4) {
		this.battleCommandCharacter4 = battleCommandCharacter4 != null ? battleCommandCharacter4 : COMMAND_DEFENSE;
	}
}