package com.github.blutorange.translune.logic;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.Fraction;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.util.Constants;

public class EffectorStatusCondition extends EffectorSingleCharacter {

	private static enum ConfusionAttack implements ISkilled {
		INSTANCE;

		@Override
		public int getAttackPower() {
			return Constants.BATTLE_BASIC_POWER_CONFUSION;
		}

		@Override
		public EElement getElement() {
			return EElement.NORMAL;
		}

		@Override
		public boolean getHighCritical() {
			return false;
		}

		@Override
		public boolean getIsPhysical() {
			return true;
		}
	}
	private final EStatusCondition condition;
	private String id = StringUtils.EMPTY;

	private int turn;

	public EffectorStatusCondition(final int player, final int character, final EStatusCondition condition) {
		super(player, character);
		this.condition = condition;
		this.turn = 0;
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

	@Override
	public boolean allowCharacterTurn(final IBattleContext context, final IComputedBattleStatus user) {
		if (id.equals(user.getCharacterState().getId())) {
			if (!condition.canMove()) {
				final CharacterState cs = user.getCharacterState();
				final BattleAction action = new BattleAction.Builder(context).character(user).targets(user)
						.addSentences(String.format(condition.getCannotMoveMessage(), cs.getNickname())).build();
				context.getBattleActions(0).add(action);
				context.getBattleActions(1).add(action);
				if (EStatusCondition.CONFUSION.equals(condition)) {
					hurtItself(user, context);
				}
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean beforeTurn(final IBattleContext context) {
		if (condition.disappears(turn)) {
			final CharacterState cs = context.getCharacterState(getPlayer(), getCharacter());
			final BattleAction action = new BattleAction.Builder(context).character(cs).targets(cs)
					.addSentences(String.format(condition.getDisappearsMessage(), cs.getNickname())).build();
			context.getBattleActions(0).add(action);
			context.getBattleActions(1).add(action);
			return true;
		}
		return false;
	}

	@Override
	public void onAdd(final IBattleContext context) {
		final String id = context.getCharacterState(getPlayer(), getCharacter()).getId();
		if (id == null)
			throw new IllegalStateException("character state does not exits: " + id);
		context.getBattleStatus(getPlayer(), getCharacter()).setStatusCondition(condition);
		this.id = id;
	}

	@Override
	public void onRemove(final IBattleContext battleContext) {
		// nothing
	}

	private void hurtItself(final IComputedBattleStatus user, final IBattleContext context) {
		final IBattleProcessing battleProcessing = ComponentFactory.getLunarComponent().battleProcessing();
		final BattleAction.Builder builder = new BattleAction.Builder(context).character(user);
		builder.addSentences(String.format("%s hurt itself in confusion!", user.getCharacterState().getNickname()));
		final IDamageResult[] damageResult = battleProcessing.computeDamage(ConfusionAttack.INSTANCE, user, user);
		battleProcessing.dealDamage(damageResult[0], user, builder.getSentences());
		final BattleAction battleAction = builder.build();
		context.getBattleActions(getPlayer()).add(battleAction);
		context.getBattleActionsOpponent(getPlayer()).add(battleAction);
	}

	private void turnEndDamage(final Fraction turnEndDamage, final IBattleContext context) {
		final IComputedBattleStatus user = context.getComputedBattleStatus(getPlayer(), getCharacter());
		final int damage = turnEndDamage.getNumerator() * user.getComputedBattleMaxHp()
				/ turnEndDamage.getDenominator();
		user.modifyHp(damage);
		final BattleAction action = new BattleAction.Builder(context).character(user).targets(user)
				.addSentences(String.format(condition.getTurnEndDamageMessage(), user.getCharacterState().getNickname(),
						Integer.valueOf(damage)))
				.build();
		context.getBattleActions(0).add(action);
		context.getBattleActions(1).add(action);
	}
}