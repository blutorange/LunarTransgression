package com.github.blutorange.translune.db;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.translune.util.PasswordStorage;
import com.github.blutorange.translune.util.PasswordStorage.CannotPerformOperationException;
import com.github.blutorange.translune.util.PasswordStorage.InvalidHashException;

@Entity
@Table(name = "player")
public class Player extends AbstractEntity {

	@NotNull
	@Size(min = 4, max = 20)
	@OneToMany(targetEntity = CharacterState.class, orphanRemoval = true, cascade = {
			CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinColumn(name = "characterstates", nullable = false, unique = false, foreignKey = @ForeignKey(name = "fk_player_charstates"))
	private Set<CharacterState> characterStates = new HashSet<>();

	@NotNull
	@Column(name = "description", nullable = false, length = 1023, unique = false)
	private String description = StringUtils.EMPTY;

	@NotNull
	@Size(min = 0, max = 30)
	@OneToMany(targetEntity = Item.class, orphanRemoval = true, cascade = {
			CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinColumn(name = "items", nullable = false, unique = false, foreignKey = @ForeignKey(name = "fk_player_items"))
	private Set<Item> items = new HashSet<>();

	@Id
	@Column(name = "nickname", nullable = false, length = 63, unique = true)
	private String nickname = StringUtils.EMPTY;

	@NotNull
	@Size(min = 1)
	@Column(name = "passwordhash", nullable = false, length = 255, unique = false)
	private String passwordHash = StringUtils.EMPTY;

	@Transient
	private Set<CharacterState> z_unmodifiableCharacterStates = Collections.unmodifiableSet(characterStates);

	@Transient
	private Set<Item> z_unmodifiableItems = Collections.unmodifiableSet(items);

	/**
	 * @return the characters
	 */
	Set<CharacterState> getCharacterStates() {
		return characterStates;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the items
	 */
	Set<Item> getItems() {
		return items;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @return the passwordHash
	 */
	String getPasswordHash() {
		return passwordHash;
	}

	/**
	 * @return the characters
	 */
	public Set<CharacterState> getCharacterStatesUnmodifiable() {
		return z_unmodifiableCharacterStates;
	}

	/**
	 * @return the items
	 */
	public Set<Item> getItemsUnmodifiable() {
		return z_unmodifiableItems;
	}

	/**
	 * @param characters
	 *            the characters to set
	 */
	void setCharacterStates(final Set<CharacterState> characterStates) {
		this.characterStates = characterStates;
		this.z_unmodifiableCharacterStates = Collections.unmodifiableSet(this.characterStates);
	}

	/**
	 * @param description
	 *            the description to set
	 */
	void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @param items
	 *            the items to set
	 */
	void setItems(final Set<Item> items) {
		this.items = items;
		this.z_unmodifiableItems = Collections.unmodifiableSet(this.items);
	}

	/**
	 * @param nickname
	 *            the nickname to set
	 */
	void setNickname(final String nickname) {
		this.nickname = nickname;
	}

	void setPassword(@NonNull final String password) throws CannotPerformOperationException {
		passwordHash = PasswordStorage.createHash(password);
	}

	/**
	 * @param passwordHash
	 *            the passwordHash to set
	 */
	void setPasswordHash(final String passwordHash) {
		this.passwordHash = passwordHash;
	}

	@Override
	public String toString() {
		return String.format("Player(%s)", nickname);
	}

	public boolean verifyPassword(@NonNull final String password) throws CannotPerformOperationException, InvalidHashException {
		final String hash = passwordHash;
		if (hash.isEmpty())
			throw new InvalidHashException("password hash must not be empty");
		return PasswordStorage.verifyPassword(password, hash);
	}

	@Override
	public Serializable getPrimaryKey() {
		return nickname;
	}

	@Override
	public EEntityMeta getEntityMeta() {
		return EEntityMeta.PLAYER;
	}

//	public boolean containsCharacter(final String characterName) {
//		for (final Character c : characters)
//			if (characterName.equals(c.getPrimaryKey()))
//				return true;
//		return false;
//	}

//	public boolean containsItem(final String itemName) {
//		for (final Item item : items)
//			if (itemName.equals(item.getPrimaryKey()))
//				return true;
//		return false;
//	}
}
