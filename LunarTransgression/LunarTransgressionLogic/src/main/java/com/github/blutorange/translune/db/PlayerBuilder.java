package com.github.blutorange.translune.db;

import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.common.CannotPerformOperationException;
import com.github.blutorange.common.PasswordStorage;
import com.github.blutorange.translune.ic.ComponentFactory;

public class PlayerBuilder {
	private final String nickname;
	@Nullable private String passwordHash = null;

	private String description = StringUtils.EMPTY;

	private final Set<CharacterState> characterStates = new HashSet<>();
	private final Set<Item> items = new HashSet<>();

	public PlayerBuilder(final String nickname) {
		this.nickname = nickname;
	}

	public PlayerBuilder addCharacterState(final CharacterState characterState) {
		characterStates.add(characterState);
		return this;
	}

	public PlayerBuilder addCharacterStates(final CharacterState... characterStates) {
		for (final CharacterState cs : characterStates)
			addCharacterState(cs);
		return this;
	}

	public PlayerBuilder addCharacterStates(final Collection<CharacterState> characterStates) {
		characterStates.addAll(characterStates);
		return this;
	}

	public PlayerBuilder addItem(final Item item) {
		items.add(item);
		return this;
	}

	public PlayerBuilder addItems(final Collection<Item> items) {
		items.addAll(items);
		return this;
	}

	public PlayerBuilder setDescription(final String description) {
		this.description = description;
		return this;
	}

	public PlayerBuilder setPassword(final String password) throws CannotPerformOperationException {
		this.passwordHash = PasswordStorage.createHash(password);
		return this;
	}

	public PlayerBuilder generateRandomPassword(final int byteCount) throws CannotPerformOperationException {
		final byte[] bytes = new byte[byteCount];
		ComponentFactory.getLogicComponent().randomSecure().nextBytes(bytes);
		return setPassword(Base64.getEncoder().encodeToString(bytes));
	}

	public Player build() {
		if (passwordHash == null)
			throw new IllegalStateException("No password was set.");
		final Player player = new Player(nickname, passwordHash, description, characterStates, items);
		for (final CharacterState cs : characterStates)
			cs.setPlayer(player);
		return player;
	}
}
