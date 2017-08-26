package com.github.blutorange.translune.logic;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.Fraction;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.socket.BattleAction;

public class EffectorStatusCondition implements IGlobalBattleEffector {

	private final int character;
	private final int player;
	private int turn;
	private final EStatusCondition condition;
	private String id = StringUtils.EMPTY;

	public EffectorStatusCondition(final int player, final int character, final EStatusCondition condition) {
		this.player = player;
		this.character = character;
		this.condition = condition;
		this.turn = 0;
	}

	@Override
	public boolean beforeTurn(final IBattleContext context) {
		if (condition.disappears(turn)) {
			final CharacterState cs = context.getCharacterState(player, character);
			final BattleAction action = new BattleAction.Builder().character(cs).targets(cs)
					.addSentences(String.format(condition.getDisappearsMessage(), cs.getNickname())).build();
			context.getBattleActions(0).add(action);
			context.getBattleActions(1).add(action);
			return true;
		}
		return false;
	}

	@Override
	public boolean allowTurn(final IBattleContext context, final CharacterState characterState) {
		if (id.equals(characterState.getId())) {
			if (!condition.canMove()) {
				final CharacterState cs = context.getCharacterState(player, character);
				final BattleAction action = new BattleAction.Builder().character(characterState).targets(characterState)
						.addSentences(String.format(condition.getCannotMoveMessage(), cs.getNickname())).build();
				context.getBattleActions(0).add(action);
				context.getBattleActions(1).add(action);
				if (EStatusCondition.CONFUSE.equals(condition)) {
					// TODO [MID] implement hurt itself
				}
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean afterTurn(final IBattleContext context) {
		final Fraction turnEndDamage = condition.getTurnEndDamage(turn);
		if (turnEndDamage.compareTo(Fraction.ZERO) > 0) {
			turnEndDamage(turnEndDamage, context);
		}
		++turn;
		return false;
	}

	private void turnEndDamage(final Fraction turnEndDamage, final IBattleContext context) {
		final IComputedBattleStatus user = context.getComputedBattleStatus(player, character);
		final int damage = turnEndDamage.getNumerator() * user.getComputedBattleMaxHp()
				/ turnEndDamage.getDenominator();
		user.modifyHp(damage);
		final BattleAction action = new BattleAction.Builder().character(user).targets(user)
				.addSentences(String.format(condition.getTurnEndDamageMessage(), user.getCharacterState().getNickname(),
						Integer.valueOf(damage)))
				.build();
		context.getBattleActions(0).add(action);
		context.getBattleActions(1).add(action);
	}

	@Override
	public void onAdd(final IBattleContext context) {
		final String id = context.getCharacterState(player, character).getId();
		if (id == null)
			throw new IllegalStateException("character state does not exits: " + id);
		context.getBattleStatus(player, character).setStatusCondition(condition);
		this.id = id;
	}

	@Override
	public void onRemove(final IBattleContext battleContext) {
		// nothing
	}
}