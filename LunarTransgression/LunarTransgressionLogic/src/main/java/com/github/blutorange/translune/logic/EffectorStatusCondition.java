package com.github.blutorange.translune.logic;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.Fraction;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.util.Constants;

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
			final BattleAction action = new BattleAction(cs.getId(), new String[0],
					String.format(condition.getDisappearsMessage(), cs.getNickname()));
			context.getBattleAction(0).add(action);
			context.getBattleAction(1).add(action);
			return true;
		}
		return false;
	}

	@Override
	public boolean allowTurn(final IBattleContext context, final CharacterState characterState) {
		if (id.equals(characterState.getId())) {
			if (!condition.canMove()) {
				final CharacterState cs = context.getCharacterState(player, character);
				final BattleAction action = new BattleAction(cs.getId(), new String[0],
						String.format(condition.getCannotMoveMessage(), cs.getNickname()));
				context.getBattleAction(0).add(action);
				context.getBattleAction(1).add(action);
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
		final BattleStatus bs = context.getBattleStatus(player, character);
		final CharacterState cs = context.getCharacterState(player, character);
		final IComputedBattleStatus cbs = context.getComputedBattleStatus(player, character);
		final int damage = turnEndDamage.getNumerator()*cbs.getComputedBattleHp()/turnEndDamage.getDenominator();
		final int newHp = bs.getHp()*cbs.getComputedBattleHp()/Constants.MAX_RELATIVE_HP - damage;
		final int newRelativeHp = newHp*Constants.MAX_RELATIVE_HP/cbs.getComputedBattleHp();
		bs.setCurrentHp(newRelativeHp);
		final BattleAction action = new BattleAction(cs.getId(), new String[0],
				String.format(condition.getTurnEndDamageMessage(), cs.getNickname(), damage));
		context.getBattleAction(0).add(action);
		context.getBattleAction(1).add(action);
	}

	@Override
	public void onAdd(final IBattleContext context) {
		id = context.getCharacterState(player, character).getId();
	}

	@Override
	public void onRemove(final IBattleContext battleContext) {
	}
}