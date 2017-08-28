package com.github.blutorange.translune.logic;

import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.translune.socket.BattleCommand;

public enum EBattleCommandType {
	BASIC_ATTACK {
		@Override
		public IBattleCommandHandler createHandler(final IBattleContext context, final int player, final int character,
				final BattleCommand command) throws IllegalArgumentException {
			return new BattleCommandHandlerBasicAttack(context, player, character, command);
		}
	},
	SKILL {
		@Override
		public IBattleCommandHandler createHandler(final IBattleContext context, final int player, final int character,
				final BattleCommand command) throws IllegalArgumentException {
			return new BattleCommandHandlerSkill(context, player, character, command);
		}
	},
	ITEM {
		@Override
		public IBattleCommandHandler createHandler(final IBattleContext context, final int player, final int character,
				final BattleCommand command) throws IllegalArgumentException {
			return new BattleCommandHandlerItem(context, player, character, command);
		}
	},
	DEFEND {
		@Override
		public IBattleCommandHandler createHandler(final IBattleContext context, final int player, final int character,
				final BattleCommand command) throws IllegalArgumentException {
			return new BattleCommandHandlerDefend(context, player, character, command);
		}
	},
	SPECIAL {
		@Override
		public IBattleCommandHandler createHandler(final IBattleContext context, final int player, final int character,
				final BattleCommand command) throws IllegalArgumentException {
			return new BattleCommandHandlerSpecial(context, player, character, command);
		}
	},
	KO {
		@Override
		public @NonNull IBattleCommandHandler createHandler(final IBattleContext context, final int player,
				final int character, final BattleCommand command) throws IllegalArgumentException {
			return new BattleCommandHandlerKo(context, player, character, command);
		}
	};

	public abstract IBattleCommandHandler createHandler(IBattleContext context, int player, int character,
			BattleCommand command) throws IllegalArgumentException;
}