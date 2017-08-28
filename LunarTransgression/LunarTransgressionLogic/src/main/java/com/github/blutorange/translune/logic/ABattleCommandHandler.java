package com.github.blutorange.translune.logic;

import javax.inject.Inject;

import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.socket.BattleCommand;

public abstract class ABattleCommandHandler implements IBattleCommandHandler {
	protected IBattleContext context;
	protected int player;
	protected int character;
	protected BattleCommand battleCommand;

	private final int battleOrderSpeedDeviation;

	@Inject
	protected IBattleProcessing battleProcessing;

	public ABattleCommandHandler(final IBattleContext battleContext, final int player, final int character,
			final BattleCommand battleCommand) {
		ComponentFactory.getLunarComponent().inject(this);
		this.context = battleContext;
		this.player = player;
		this.character = character;
		this.battleCommand = battleCommand;
		this.battleOrderSpeedDeviation = ComponentFactory.getLunarComponent().randomBasic().get().nextInt(21) + 90;
	}

	@Override
	public int getPlayerIndex() {
		return player;
	}

	@Override
	public int getCharacterIndex() {
		return character;
	}

	@Override
	public int getSpeed() {
		return context.getComputedBattleStatus(player, character).getComputedBattleSpeed() * battleOrderSpeedDeviation
				/ 100;
	}

	@Override
	public String getUser() {
		return context.getCharacterState(player, character).getPlayer().getNickname();
	}

	@Override
	public CharacterState getCharacterState() {
		return context.getCharacterState(player, character);
	}

	@Override
	public IComputedBattleStatus getComputedBattleStatus() {
		return context.getComputedBattleStatus(player, character);
	}

	@Override
	public @NonNull IBattleContext getBattleContext() {
		return context;
	}
}