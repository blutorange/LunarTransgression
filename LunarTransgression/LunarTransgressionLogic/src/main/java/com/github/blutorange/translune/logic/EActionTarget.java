package com.github.blutorange.translune.logic;

import java.util.Random;

import com.github.blutorange.translune.db.CharacterState;
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
		public CharacterState[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			return context.getCharacterStatesOpponent(player);
		}
	},

	/**
	 * Move targets one character except for the character that uses this
	 * move. Player must select one target.
	 */
	ALL_OTHER_POKEMON {
		@Override
		public CharacterState[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			final String[] targets = battleCommand.getTargets();
			if (targets.length != 1)
				return new CharacterState[0];
			final int[] characterIndex = context.getCharacterIndex(targets[0]);
			if (characterIndex[0] == player && characterIndex[1] == character) {
				return new CharacterState[0];
			}
			return new CharacterState[]{ context.getCharacterState(characterIndex) };
		}
	},

	/**
	 * Targets every chararacter on the field. Player must not specify a target.
	 */
	ALL_POKEMON {
		@Override
		public CharacterState[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			return ObjectArrays.concat(context.getCharacterStates(0), context.getCharacterStates(1), CharacterState.class);
		}
	},

	/**
	 * Targets one character of the player except for the character that used
	 * this move. Player must select one target.
	 */
	ALLY {
		@Override
		public CharacterState[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			final String[] targets = battleCommand.getTargets();
			if (targets.length != 1)
				return new CharacterState[0];
			final int[] characterIndex = context.getCharacterIndex(targets[0]);
			if (characterIndex[0] != player || characterIndex[1] == character) {
				return new CharacterState[0];
			}
			return new CharacterState[]{ context.getCharacterState(characterIndex) };
		}
	},

	/**
	 * Move targets all characters. Player must not specify a target.
	 */
	ENTIRE_FIELD {
		@Override
		public CharacterState[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			return EActionTarget.ALL_POKEMON.getTargets(context, battleCommand, player, character);
		}
	},

	/**
	 * Targets one character of the opponent. User must select one target.
	 */
	OPPONENTS_FIELD {
		@Override
		public CharacterState[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			final String[] targets = battleCommand.getTargets();
			if (targets.length != 1)
				return new CharacterState[0];
			final int[] characterIndex = context.getCharacterIndex(targets[0]);
			if (characterIndex[0] == player) {
				return new CharacterState[0];
			}
			return new CharacterState[]{ context.getCharacterState(characterIndex) };
		}
	},

	/**
	 * Move targets a random character of the opponent. Player must not specify
	 * a target.
	 */
	RANDOM_OPPONENT {
		@Override
		public CharacterState[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			final CharacterState[] characterStates = context.getCharacterStatesOpponent(player);
			return new CharacterState[]{ characterStates[random.nextInt(characterStates.length)] };
		}
	},

	/**
	 * Move targets one character, which must be specified by the player.
	 */
	SELECTED_POKEMON {
		@Override
		public CharacterState[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			final String[] targets = battleCommand.getTargets();
			if (targets.length != 1)
				return new CharacterState[0];
			final int[] characterIndex = context.getCharacterIndex(targets[0]);
			return new CharacterState[]{ context.getCharacterState(characterIndex) };
		}
	},

	/**
	 * Target is determined by the move in a complex fashion. Player must not
	 * selected a target.
	 */
	SELECTED_POKEMON_ME_FIRST {
		@Override
		public CharacterState[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			return new CharacterState[0];
		}
	},

	/**
	 * Target is determined by the move in a complex fashion. Player must not
	 * selected a target.
	 */
	SPECIFIC_MOVE {
		@Override
		public CharacterState[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			return new CharacterState[0];
		}
	},

	/**
	 * Move targets the character that used this move. Player must not specify a
	 * target.
	 */
	USER {
		@Override
		public CharacterState[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			return new CharacterState[] { context.getCharacterState(player, character) };
		}
	},

	/**
	 * Targets all characters of the player. Player must not specify a target.
	 */
	USER_AND_ALLIES {
		@Override
		public CharacterState[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			return context.getCharacterStates(player);
		}
	},

	/**
	 * Targets one character of the player. Player must select one target.
	 */
	USER_OR_ALLY {
		@Override
		public CharacterState[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			return USERS_FIELD.getTargets(context, battleCommand, player, character);
		}
	},

	/**
	 * Targets on character of the player. User must select one target.
	 */
	USERS_FIELD {
		@Override
		public CharacterState[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
				final int player, final int character) {
			final String[] targets = battleCommand.getTargets();
			if (targets.length != 1)
				return new CharacterState[0];
			final int[] characterIndex = context.getCharacterIndex(targets[0]);
			if (characterIndex[0] != player) {
				return new CharacterState[0];
			}
			return new CharacterState[]{ context.getCharacterState(characterIndex) };
		}
	},;

	private static Random random = ComponentFactory.getLogicComponent().randomBasic();

	public abstract CharacterState[] getTargets(final IBattleContext context, final BattleCommand battleCommand,
			int player, int character);
}