package com.github.blutorange.translune.logic;

import java.util.Optional;

import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.socket.BattleCommand;

public class BattleCommandHandlerItem extends ABattleCommandHandler {

	private final Item item;

	public BattleCommandHandlerItem(final IBattleContext battleContext, final int player, final int character,
			final BattleCommand battleCommand) {
		super(battleContext, player, character, battleCommand);
		final Optional<Item> item = context.getCharacterState(player, character).getPlayer().getUnmodifiableItems().stream()
				.filter(i -> i.getPrimaryKey().equals(battleCommand.getAction())).findAny();
		if (!item.isPresent())
			throw new IllegalArgumentException("player does not possess item");
		this.item = item.get();
	}

	@Override
	public int getPriority() {
		return item.getPriority();
	}

	@Override
	public void preProcess() {
		// not needed
	}

	@Override
	public void execute() {
		item.getEffect().execute(item, context, battleCommand, player, character);
	}

	@Override
	public void postProcess() {
		// not needed
	}
}