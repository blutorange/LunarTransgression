package com.github.blutorange.translune.logic;

import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.socket.BattleCommand;

public class BattleCommandHandlerSkill extends ABattleCommandHandler {

	private final Skill skill;

	public BattleCommandHandlerSkill(final IBattleContext battleContext, final int player, final int character,
			final BattleCommand battleCommand) {
		super(battleContext, player, character, battleCommand);
		final Optional<Entry<Skill, Integer>> entry = context.getCharacterState(player, character).getCharacter()
				.getUnmodifiableSkills().entrySet().stream().filter(e -> e.getKey().getPrimaryKey().equals(battleCommand.getAction()))
				.findAny();
		if (!entry.isPresent())
			throw new IllegalArgumentException("character does not know skill");
		final Entry<Skill, Integer> skill = entry.get();
		if (context.getCharacterState(player, character).getLevel() < skill.getValue().intValue())
			throw new IllegalArgumentException("character does not know skill yet");
		this.skill = skill.getKey();
	}

	@Override
	public int getPriority() {
		return skill.getPriority();
	}

	@Override
	public void preProcess() {
	}

	@Override
	public void execute(@NonNull final List<com.github.blutorange.translune.socket.BattleAction> battleActionsMe,
			@NonNull final List<com.github.blutorange.translune.socket.BattleAction> battleActionsHim) {
		// TODO Implement skill
		throw new RuntimeException("TODO - not yet implemented");
	}

	@Override
	public void postProcess() {
	}
}
