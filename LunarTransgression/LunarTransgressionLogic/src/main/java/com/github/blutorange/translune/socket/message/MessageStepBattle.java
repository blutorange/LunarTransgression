package com.github.blutorange.translune.socket.message;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.logic.IBattleCommand;
import com.github.blutorange.translune.logic.IBattleCommand.EBattleCommand;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;

public class MessageStepBattle implements ILunarPayload {
	private IBattleCommand battleCommandCharacter1 = EBattleCommand.DUMMY;
	private IBattleCommand battleCommandCharacter2 = EBattleCommand.DUMMY;
	private IBattleCommand battleCommandCharacter3 = EBattleCommand.DUMMY;
	private IBattleCommand battleCommandCharacter4 = EBattleCommand.DUMMY;

	@Deprecated
	public MessageStepBattle() {
	}

	public MessageStepBattle(final IBattleCommand battleCommandCharacter1, final IBattleCommand battleCommandCharacter2,
			final IBattleCommand battleCommandCharacter3, final IBattleCommand battleCommandCharacter4) {
		this.battleCommandCharacter1 = battleCommandCharacter1;
		this.battleCommandCharacter2 = battleCommandCharacter2;
		this.battleCommandCharacter3 = battleCommandCharacter3;
		this.battleCommandCharacter4 = battleCommandCharacter4;
	}

	/**
	 * @return the battleCommandCharacter1
	 */
	public IBattleCommand getBattleCommandCharacter1() {
		return battleCommandCharacter1;
	}

	/**
	 * @return the battleCommandCharacter2
	 */
	public IBattleCommand getBattleCommandCharacter2() {
		return battleCommandCharacter2;
	}

	/**
	 * @return the battleCommandCharacter3
	 */
	public IBattleCommand getBattleCommandCharacter3() {
		return battleCommandCharacter3;
	}

	/**
	 * @return the battleCommandCharacter4
	 */
	public IBattleCommand getBattleCommandCharacter4() {
		return battleCommandCharacter4;
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.STEP_BATTLE;
	}

	/**
	 * @param battleCommandCharacter1
	 *            the battleCommandCharacter1 to set
	 */
	public void setBattleCommandCharacter1(@Nullable final IBattleCommand battleCommandCharacter1) {
		this.battleCommandCharacter1 = battleCommandCharacter1 != null ? battleCommandCharacter1 : EBattleCommand.DUMMY;
		;
	}

	/**
	 * @param battleCommandCharacter2
	 *            the battleCommandCharacter2 to set
	 */
	public void setBattleCommandCharacter2(@Nullable final IBattleCommand battleCommandCharacter2) {
		this.battleCommandCharacter2 = battleCommandCharacter2 != null ? battleCommandCharacter2 : EBattleCommand.DUMMY;
		;
	}

	/**
	 * @param battleCommandCharacter3
	 *            the battleCommandCharacter3 to set
	 */
	public void setBattleCommandCharacter3(@Nullable final IBattleCommand battleCommandCharacter3) {
		this.battleCommandCharacter3 = battleCommandCharacter3 != null ? battleCommandCharacter3 : EBattleCommand.DUMMY;
		;
	}

	/**
	 * @param battleCommandCharacter4
	 *            the battleCommandCharacter4 to set
	 */
	public void setBattleCommandCharacter4(@Nullable final IBattleCommand battleCommandCharacter4) {
		this.battleCommandCharacter4 = battleCommandCharacter4 != null ? battleCommandCharacter4 : EBattleCommand.DUMMY;
	}
}