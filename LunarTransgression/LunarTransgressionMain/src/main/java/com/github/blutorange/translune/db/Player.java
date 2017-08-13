package com.github.blutorange.translune.db;

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
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.PasswordStorage;
import com.github.blutorange.translune.PasswordStorage.CannotPerformOperationException;
import com.github.blutorange.translune.PasswordStorage.InvalidHashException;

@Entity
@Table(name = "player")
public class Player extends AbstractEntity {
	@NotNull
	@Size(min = 4, max = 20)
	@OneToMany(targetEntity = Character.class, orphanRemoval = true, cascade = {
			CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinColumn(name = "player", nullable = false, unique = false, foreignKey = @ForeignKey(name = "fk_player_chars"))
	private Set<Character> characters = new HashSet<>();

	@NotNull
	@Column(name = "description", nullable = false, length = 1023, unique = false)
	private String description = StringUtils.EMPTY;

	@NotNull
	@Size(min = 0, max = 99)
	@OneToMany(targetEntity = Item.class, orphanRemoval = true, cascade = {
			CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinColumn(name = "player", nullable = false, unique = false, foreignKey = @ForeignKey(name = "fk_player_items"))
	private Set<Item> items = new HashSet<>();

	@Id
	@Column(name = "nickname", nullable = false, length = 63, unique = true)
	private String nickname = StringUtils.EMPTY;

	@NotNull
	@Size(min = 1)
	@Column(name = "passwordhash", nullable = false, length = 255, unique = false)
	private String passwordHash = StringUtils.EMPTY;

	@Transient
	private Set<Character> z_unmodifiableCharacters = Collections.unmodifiableSet(characters);

	@Transient
	private Set<Item> z_unmodifiableItems = Collections.unmodifiableSet(items);

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(@Nullable final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Player))
			return false;
		final Player other = (Player) obj;
		if (!nickname.equals(other.nickname))
			return false;
		return true;
	}

	/**
	 * @return the characters
	 */
	Set<Character> getCharacters() {
		return characters;
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
	public Set<Character> getUnmodifiableCharacters() {
		return z_unmodifiableCharacters;
	}

	/**
	 * @return the items
	 */
	public Set<Item> getUnmodifiableItems() {
		return z_unmodifiableItems;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + nickname.hashCode();
		return result;
	}

	/**
	 * @param characters
	 *            the characters to set
	 */
	void setCharacters(final Set<Character> characters) {
		this.characters = characters;
		this.z_unmodifiableCharacters = Collections.unmodifiableSet(this.characters);
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

	void setPassword(final String password) throws CannotPerformOperationException {
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

	public boolean verifyPassword(final String password) throws CannotPerformOperationException, InvalidHashException {
		final String hash = passwordHash;
		if (hash.isEmpty())
			throw new InvalidHashException("password hash must not be empty");
		return PasswordStorage.verifyPassword(password, hash);
	}
}
