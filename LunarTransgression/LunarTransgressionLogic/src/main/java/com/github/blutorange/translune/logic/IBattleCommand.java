package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.db.CharacterState;

public interface IBattleCommand {
	boolean appliesTo(CharacterState characterState);
}
