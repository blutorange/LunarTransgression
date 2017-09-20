package com.github.blutorange.translune.db;

import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.Builder;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.common.CannotPerformOperationException;
import com.github.blutorange.common.PasswordStorage;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.util.Constants;

public class PlayerBuilder implements Builder<Player> {
	private final Set<CharacterState> characterStates = new HashSet<>();

	private final Set<CharacterState> releasedCharacterStates = new HashSet<>();

	private String description = StringUtils.EMPTY;

	private String imgAvatar = Constants.DEFAULT_PLAYER_AVATAR;

	private final Set<Item> items = new HashSet<>();

	private final String nickname;

	@Nullable
	private String passwordHash = null;

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

	public PlayerBuilder addReleasedCharacterState(final CharacterState characterState) {
		releasedCharacterStates.add(characterState);
		return this;
	}

	public PlayerBuilder addReleasedCharacterStates(final CharacterState... characterStates) {
		for (final CharacterState cs : releasedCharacterStates)
			addReleasedCharacterState(cs);
		return this;
	}

	public PlayerBuilder addReleasedCharacterStates(final Collection<CharacterState> characterStates) {
		releasedCharacterStates.addAll(characterStates);
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

	@Override
	public Player build() {
		final String nickname = this.nickname;
		String imgAvatar = this.imgAvatar;
		final String passwordHash = this.passwordHash;
		if (passwordHash == null)
			throw new IllegalStateException("No password was set.");
		if (nickname == null)
			throw new IllegalStateException("No nickname was set.");
		if (imgAvatar == null)
			imgAvatar = Constants.DEFAULT_PLAYER_AVATAR;
		final Player player = new Player();
		player.setCharacterStates(characterStates);
		player.setDescription(description);
		player.setImgAvatar(imgAvatar);
		player.setItems(items);
		player.setNickname(nickname);
		player.setPasswordHash(passwordHash);
		for (final CharacterState cs : characterStates) {
			cs.setPlayer(player);
		}
		for (final CharacterState cs : releasedCharacterStates) {
			cs.setReleasedPlayer(player);
		}
		return player;
	}

	public PlayerBuilder generateRandomPassword(final int byteCount) throws CannotPerformOperationException {
		final byte[] bytes = new byte[byteCount];
		ComponentFactory.getLunarComponent().randomSecure().get().nextBytes(bytes);
		return setPassword(Base64.getEncoder().encodeToString(bytes));
	}

	public PlayerBuilder setDescription(final String description) {
		this.description = description;
		return this;
	}

	public PlayerBuilder setImgAvatar(final String imgAvatar) {
		this.imgAvatar = imgAvatar;
		return this;
	}

	public PlayerBuilder setPassword(final String password) throws CannotPerformOperationException {
		this.passwordHash = PasswordStorage.createHash(password);
		return this;
	}
}