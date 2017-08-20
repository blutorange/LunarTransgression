package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.db.CharacterState;

public interface IBattleCommand {
	public static enum EBattleCommand implements IBattleCommand {
		DUMMY;
		@Override
		public boolean appliesTo(final CharacterState characterState) {
			return false;
		}
	}

	boolean appliesTo(CharacterState characterState);
}
