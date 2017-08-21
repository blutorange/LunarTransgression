package com.github.blutorange.translune.logic;

import com.github.blutorange.translune.socket.BattleCommand;

public enum EBattleCommandType {
	BASIC_ATTACK {
		@Override
		public IBattleCommandHandler createHandler(final IBattleContext battleContext, final int player,
				final int character, final BattleCommand battleCommand) throws IllegalArgumentException {
			return new BattleCommandHandlerBasicAttack(battleContext, player, character, battleCommand);
		}
	},
	SKILL {
		@Override
		public IBattleCommandHandler createHandler(final IBattleContext battleContext, final int player,
				final int character, final BattleCommand battleCommand) throws IllegalArgumentException {
			return new BattleCommandHandlerSkill(battleContext, player, character, battleCommand);
		}
	},
	ITEM {
		@Override
		public IBattleCommandHandler createHandler(final IBattleContext battleContext, final int player,
				final int character, final BattleCommand battleCommand) throws IllegalArgumentException {
			return new BattleCommandHandlerItem(battleContext, player, character, battleCommand);
		}
	},
	DEFEND {
		@Override
		public IBattleCommandHandler createHandler(final IBattleContext battleContext, final int player,
				final int character, final BattleCommand battleCommand) throws IllegalArgumentException {
			return new BattleCommandHandlerDefend(battleContext, player, character, battleCommand);
		}
	},
	SPECIAL {
		@Override
		public IBattleCommandHandler createHandler(final IBattleContext battleContext, final int player,
				final int character, final BattleCommand battleCommand) throws IllegalArgumentException {
			return new BattleCommandHandlerSpecial(battleContext, player, character, battleCommand);
		}
	};

	public abstract IBattleCommandHandler createHandler(IBattleContext battleContext, int player, int character, BattleCommand action) throws IllegalArgumentException;
}
