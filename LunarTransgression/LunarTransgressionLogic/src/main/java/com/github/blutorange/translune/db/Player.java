package com.github.blutorange.translune.db;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.common.IAccessible;
import com.github.blutorange.common.PasswordStorage;
import com.github.blutorange.common.PasswordStorage.CannotPerformOperationException;
import com.github.blutorange.common.PasswordStorage.InvalidHashException;
import com.github.blutorange.translune.util.Constants;

@Entity
@Table(name = "player")
public class Player extends AbstractStoredEntity {
	@Deprecated
	public Player() {
	}

	public Player(final String nickname, final String passwordHash, final String description,
			final Set<CharacterState> characterStates, final Set<Item> items) {
		this.nickname = nickname;
		this.description = description;
		this.characterStates = characterStates;
		this.items = items;
		this.passwordHash = passwordHash;
		this.releasedCharacterStates = new HashSet<>();
	}

	@NotNull
	@Size(min = Constants.MIN_CHARACTERS, max = Constants.MAX_CHARACTERS)
	@OneToMany(targetEntity = CharacterState.class, orphanRemoval = true, cascade = {}, fetch = FetchType.LAZY, mappedBy = "player")
	private Set<CharacterState> characterStates = new HashSet<>();

	@NotNull
	@OneToMany(targetEntity = CharacterState.class, orphanRemoval = true, cascade = {}, fetch = FetchType.LAZY, mappedBy = "player")
	@Size(min = 0)
	private Set<CharacterState> releasedCharacterStates;

	@NotNull
	@Column(name = "description", nullable = false, length = 1023, unique = false)
	private String description = StringUtils.EMPTY;

	@NotNull
	@Size(min = Constants.MIN_ITEMS, max = Constants.MAX_ITEMS)
	@ManyToMany(targetEntity = Item.class, cascade = {}, fetch = FetchType.LAZY)
	@JoinTable(name = "playeritem", joinColumns = @JoinColumn(name = "playeritem_player", nullable = false, foreignKey = @ForeignKey(name = "fk_playeritem_player")), inverseJoinColumns = @JoinColumn(name = "playeritem_item", foreignKey = @ForeignKey(name = "fk_playeritem_item")))
	private Set<Item> items = new HashSet<>();

	@Id
	@Column(name = "nickname", nullable = false, length = 63, unique = true, updatable = false)
	private String nickname = StringUtils.EMPTY;

	@NotNull
	@Size(min = 1)
	@Column(name = "passwordhash", nullable = false, length = 255, unique = false)
	private String passwordHash = StringUtils.EMPTY;

	void addCharacterState(final CharacterState characterState) {
		characterStates.add(characterState);
		characterState.setPlayer(this);
	}

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
	public Set<CharacterState> getUnmodifiableCharacterStates() {
		return characterStates;
	}

	/**
	 * @return the items
	 */
	public Set<Item> getUnmodifiableItems() {
		return items;
	}

	/**
	 * @param characters
	 *            the characters to set
	 */
	void setCharacterStates(final Set<CharacterState> characterStates) {
		this.characterStates = characterStates;
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
	 * @return the releasedCharacterStates
	 */
	Set<CharacterState> getReleasedCharacterStates() {
		return releasedCharacterStates;
	}

	/**
	 * @param releasedCharacterStates
	 *            the releasedCharacterStates to set
	 */
	void setReleasedCharacterStates(final Set<CharacterState> releasedCharacterStates) {
		this.releasedCharacterStates = releasedCharacterStates;
	}

	/**
	 * @return the releasedCharacterStates
	 */
	public Set<CharacterState> getUnmodifiableReleasedCharacterStates() {
		return releasedCharacterStates;
	}

	public int getReleasedCharacterStatesCount() {
		return releasedCharacterStates.size();
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

	public boolean verifyPassword(@NonNull final String password)
			throws CannotPerformOperationException, InvalidHashException {
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

	void addItem(final Item item) {
		items.add(item);
	}

	@Override
	void forEachAssociatedObject(final Consumer<IAccessible<AbstractStoredEntity>> consumer) {
		associated(characterStates, consumer);
		associated(items, consumer);
		associated(releasedCharacterStates, consumer);
	}

	public void removeItem(final Item item) {
		items.remove(item);
	}

	public void removeCharacterState(final CharacterState characterState) {
		characterStates.remove(characterState);
		characterState.setPlayer(null);
	}
}