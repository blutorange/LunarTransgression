package com.github.blutorange.translune.db;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.common.CannotPerformOperationException;

public class ModifiablePlayer extends ModifiableEntity<Player> {
	public void addCharacterState(final CharacterState characterState) {
		entity.addCharacterState(characterState);
	}

	public void addReleasedCharacterState(final CharacterState characterState) {
		entity.addReleasedCharacterState(characterState);
	}

	public void addItem(final Item item) {
		entity.addItem(item);
	}

	public Set<CharacterState> getReleasedCharacterStates() {
		return entity.getReleasedCharacterStates();
	}

	public void removeCharacterState(final CharacterState characterState) {
		entity.removeCharacterState(characterState);
	}

	public void removeItem(final Item item) {
		entity.removeItem(item);
	}

	public void setCharacterStates(final Set<CharacterState> characterStates) {
		entity.setCharacterStates(characterStates);
	}

	public void setDescription(final String description) {
		entity.setDescription(description);
	}

	public void setImgAvatar(@NonNull final String imgAvatar) {
		entity.setImgAvatar(imgAvatar);
	}

	public void setItems(final Set<Item> items) {
		entity.setItems(items);
	}

	public void setNickname(@NonNull final String nickname) {
		entity.setNickname(nickname);
	}

	public void setPassword(@NonNull final String password) throws CannotPerformOperationException {
		entity.setPassword(password);
	}

	public void setReleasedCharacterStates(final Set<CharacterState> releasedCharacterStates) {
		entity.setReleasedCharacterStates(releasedCharacterStates);
	}
}
