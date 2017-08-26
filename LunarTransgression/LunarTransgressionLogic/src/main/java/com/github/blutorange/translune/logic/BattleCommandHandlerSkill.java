package com.github.blutorange.translune.logic;

import java.util.Map.Entry;
import java.util.Optional;

import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.socket.BattleCommand;

public class BattleCommandHandlerSkill extends ABattleCommandHandler {

	private final Skill skill;

	public BattleCommandHandlerSkill(final IBattleContext battleContext, final int player, final int character,
			final BattleCommand battleCommand) {
		super(battleContext, player, character, battleCommand);
		@SuppressWarnings("null")
		final Optional<Entry<Skill, Integer>> entry = context.getCharacterState(player, character).getCharacter()
				.getUnmodifiableSkills().entrySet().stream()
				.filter(e -> e.getKey().getPrimaryKey().equals(battleCommand.getAction())).findAny();
		if (!entry.isPresent())
			throw new IllegalArgumentException("character does not know skill: " + battleCommand.getAction());
		final Entry<Skill, Integer> skill = entry.get();
		if (context.getCharacterState(player, character).getLevel() < skill.getValue().intValue())
			throw new IllegalArgumentException("character does not know skill yet: " + battleCommand.getAction());
		this.skill = skill.getKey();
	}

	@Override
	public int getPriority() {
		return skill.getPriority();
	}

	@Override
	public void preProcess() {
		// not needed
	}

	@Override
	public void execute() {
		skill.getEffect().execute(skill, context, battleCommand, player, character);

	}

	@Override
	public void postProcess() {
		// not needed
	}
}
