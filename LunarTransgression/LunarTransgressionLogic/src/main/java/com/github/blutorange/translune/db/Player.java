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

import com.github.blutorange.common.CannotPerformOperationException;
import com.github.blutorange.common.IAccessible;
import com.github.blutorange.common.PasswordStorage;
import com.github.blutorange.common.PasswordStorage.InvalidHashException;
import com.github.blutorange.translune.util.Constants;
import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;

@Entity
@Table(name = "player")
public class Player extends AbstractStoredEntity {
	@NotNull
	@Size(min = Constants.MIN_CHARACTERS, max = Constants.MAX_CHARACTERS)
	@OneToMany(targetEntity = CharacterState.class, orphanRemoval = true, cascade = {}, fetch = FetchType.LAZY, mappedBy = "player")
	private Set<CharacterState> characterStates = new HashSet<>();

	@NotNull
	@Size(min = 1, max = 2048)
	@Column(name = "description", nullable = false, length = 2048, unique = false)
	private String description = StringUtils.EMPTY;

	@NotNull
	@Size(min = Constants.MIN_ITEMS, max = Constants.MAX_ITEMS)
	@ManyToMany(targetEntity = Item.class, cascade = {}, fetch = FetchType.LAZY)
	@JoinTable(name = "playeritem", joinColumns = @JoinColumn(name = "playeritem_player", nullable = false, foreignKey = @ForeignKey(name = "fk_playeritem_player")), inverseJoinColumns = @JoinColumn(name = "playeritem_item", foreignKey = @ForeignKey(name = "fk_playeritem_item")))
	private Set<Item> items = new HashSet<>();

	@NonNull
	@Id
	@Column(name = "nickname", nullable = false, length = 63, unique = true, updatable = false)
	private String nickname = StringUtils.EMPTY;

	@NotNull
	@Size(min = 1)
	@Column(name = "passwordhash", nullable = false, length = 255, unique = false)
	private String passwordHash = StringUtils.EMPTY;

	@NotNull
	@OneToMany(targetEntity = CharacterState.class, orphanRemoval = true, cascade = {}, fetch = FetchType.LAZY, mappedBy = "player")
	@Size(min = 0)
	@JsonIgnore
	private Set<CharacterState> releasedCharacterStates;

	public Player() {
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	@JsonIgnore
	@Override
	public EEntityMeta getEntityMeta() {
		return EEntityMeta.PLAYER;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	@NonNull
	@Override
	@JsonIgnore
	public Serializable getPrimaryKey() {
		return nickname;
	}

	@JsonIgnore
	public int getReleasedCharacterStatesCount() {
		return releasedCharacterStates.size();
	}

	/**
	 * @return the characters
	 */
	@JsonProperty(from = "characterStates", to = "characterStates")
	public Set<CharacterState> getUnmodifiableCharacterStates() {
		return characterStates;
	}

	/**
	 * @return the items
	 */
	@JsonProperty(value = "items", to = "items", from = "items")
	public Set<Item> getUnmodifiableItems() {
		return items;
	}

	/**
	 * @return the releasedCharacterStates
	 */
	@JsonIgnore
	public Set<CharacterState> getUnmodifiableReleasedCharacterStates() {
		return releasedCharacterStates;
	}

	public void removeCharacterState(final CharacterState characterState) {
		characterStates.remove(characterState);
		characterState.setPlayer(null);
	}

	public void removeItem(final Item item) {
		items.remove(item);
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

	void addCharacterState(final CharacterState characterState) {
		characterStates.add(characterState);
		characterState.setPlayer(this);
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

	/**
	 * @return the characters
	 */
	Set<CharacterState> getCharacterStates() {
		return characterStates;
	}

	/**
	 * @return the items
	 */
	Set<Item> getItems() {
		return items;
	}

	/**
	 * @return the passwordHash
	 */
	@JsonIgnore
	String getPasswordHash() {
		return passwordHash;
	}

	/**
	 * @return the releasedCharacterStates
	 */
	@JsonIgnore
	Set<CharacterState> getReleasedCharacterStates() {
		return releasedCharacterStates;
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
	void setNickname(@NonNull final String nickname) {
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

	/**
	 * @param releasedCharacterStates
	 *            the releasedCharacterStates to set
	 */
	void setReleasedCharacterStates(final Set<CharacterState> releasedCharacterStates) {
		this.releasedCharacterStates = releasedCharacterStates != null ? releasedCharacterStates : new HashSet<>();
	}
}