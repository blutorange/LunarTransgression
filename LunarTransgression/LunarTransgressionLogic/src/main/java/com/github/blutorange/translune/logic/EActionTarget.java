package com.github.blutorange.translune.logic;

import java.util.Random;

import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.socket.BattleCommand;
import com.google.common.collect.ObjectArrays;

public enum EActionTarget {
	/**
	 * Move targets all characters of the opponent. Player must not specify a
	 * target.
	 */
	ALL_OPPONENTS {
		@Override
		public IComputedBattleStatus[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			return context.getComputedBattleStatusOpponent(player);
		}
	},

	/**
	 * Move targets one character except for the character that uses this move.
	 * Player must select one target.
	 */
	ALL_OTHER_POKEMON {
		@Override
		public IComputedBattleStatus[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			final String[] targets = battleCommand.getTargets();
			if (targets.length != 1)
				return new IComputedBattleStatus[0];
			final int[] characterIndex = context.getCharacterIndex(targets[0]);
			if (characterIndex[0] == player && characterIndex[1] == character) {
				return new IComputedBattleStatus[0];
			}
			return new IComputedBattleStatus[] { context.getComputedBattleStatus(characterIndex) };
		}
	},

	/**
	 * Targets every chararacter on the field. Player must not specify a target.
	 */
	ALL_POKEMON {
		@Override
		public IComputedBattleStatus[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			return ObjectArrays.concat(context.getComputedBattleStatus(0), context.getComputedBattleStatus(1),
					IComputedBattleStatus.class);
		}
	},

	/**
	 * Targets one character of the player except for the character that used
	 * this move. Player must select one target.
	 */
	ALLY {
		@Override
		public IComputedBattleStatus[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			final String[] targets = battleCommand.getTargets();
			if (targets.length != 1)
				return new IComputedBattleStatus[0];
			final int[] characterIndex = context.getCharacterIndex(targets[0]);
			if (characterIndex[0] != player || characterIndex[1] == character) {
				return new IComputedBattleStatus[0];
			}
			return new IComputedBattleStatus[] { context.getComputedBattleStatus(characterIndex) };
		}
	},

	/**
	 * Move targets all characters. Player must not specify a target.
	 */
	ENTIRE_FIELD {
		@Override
		public IComputedBattleStatus[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			return EActionTarget.ALL_POKEMON.getTargets(context, battleCommand, player, character);
		}
	},

	/**
	 * Targets one character of the opponent. User must select one target.
	 */
	OPPONENTS_FIELD {
		@Override
		public IComputedBattleStatus[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			final String[] targets = battleCommand.getTargets();
			if (targets.length != 1)
				return new IComputedBattleStatus[0];
			final int[] characterIndex = context.getCharacterIndex(targets[0]);
			if (characterIndex[0] == player) {
				return new IComputedBattleStatus[0];
			}
			return new IComputedBattleStatus[] { context.getComputedBattleStatus(characterIndex) };
		}
	},

	/**
	 * Move targets a random character of the opponent. Player must not specify
	 * a target.
	 */
	RANDOM_OPPONENT {
		@Override
		public IComputedBattleStatus[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			final IComputedBattleStatus[] computedBattleStatus = context.getComputedBattleStatusOpponent(player);
			return new IComputedBattleStatus[] { computedBattleStatus[random.nextInt(computedBattleStatus.length)] };
		}
	},

	/**
	 * Move targets one character, which must be specified by the player.
	 */
	SELECTED_POKEMON {
		@Override
		public IComputedBattleStatus[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			final String[] targets = battleCommand.getTargets();
			if (targets.length != 1)
				return new IComputedBattleStatus[0];
			final int[] characterIndex = context.getCharacterIndex(targets[0]);
			return new IComputedBattleStatus[] { context.getComputedBattleStatus(characterIndex) };
		}
	},

	/**
	 * Target is determined by the move in a complex fashion. Player must not
	 * selected a target.
	 */
	SELECTED_POKEMON_ME_FIRST {
		@Override
		public IComputedBattleStatus[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			return new IComputedBattleStatus[0];
		}
	},

	/**
	 * Target is determined by the move in a complex fashion. Player must not
	 * selected a target.
	 */
	SPECIFIC_MOVE {
		@Override
		public IComputedBattleStatus[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			return new IComputedBattleStatus[0];
		}
	},

	/**
	 * Move targets the character that used this move. Player must not specify a
	 * target.
	 */
	USER {
		@Override
		public IComputedBattleStatus[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			return new IComputedBattleStatus[] { context.getComputedBattleStatus(player, character) };
		}
	},

	/**
	 * Targets all characters of the player. Player must not specify a target.
	 */
	USER_AND_ALLIES {
		@Override
		public IComputedBattleStatus[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			return context.getComputedBattleStatus(player);
		}
	},

	/**
	 * Targets one character of the player. Player must select one target.
	 */
	USER_OR_ALLY {
		@Override
		public IComputedBattleStatus[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			return USERS_FIELD.getTargets(context, battleCommand, player, character);
		}
	},

	/**
	 * Targets on character of the player. User must select one target.
	 */
	USERS_FIELD {
		@Override
		public IComputedBattleStatus[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			final String[] targets = battleCommand.getTargets();
			if (targets.length != 1)
				return new IComputedBattleStatus[0];
			final int[] characterIndex = context.getCharacterIndex(targets[0]);
			if (characterIndex[0] != player) {
				return new IComputedBattleStatus[0];
			}
			return new IComputedBattleStatus[] { context.getComputedBattleStatus(characterIndex) };
		}
	},;

	private static Random random = ComponentFactory.getLogicComponent().randomBasic();

	public abstract IComputedBattleStatus[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
			int player, int character);
}