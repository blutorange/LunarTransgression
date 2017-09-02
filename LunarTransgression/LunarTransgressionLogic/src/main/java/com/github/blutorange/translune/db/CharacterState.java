package com.github.blutorange.translune.db;

import java.io.Serializable;
import java.util.UUID;
import java.util.function.Consumer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.common.IAccessible;
import com.github.blutorange.translune.logic.ENature;
import com.github.blutorange.translune.logic.IComputedStatus;
import com.github.blutorange.translune.util.Constants;
import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;

@Entity
@Table(name = "charstate")
public class CharacterState extends AbstractStoredEntity {
	@NotNull
	@ManyToOne(targetEntity = Character.class, cascade = {}, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "\"character\"", updatable = false, insertable = true, nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_charstate_char"))
	private Character character;

	@Min(0)
	@Max(Constants.MAX_EXP)
	@Column(name = "exp", nullable = false, unique = false, updatable = true, insertable = true)
	private int exp;

	@NonNull
	@NotNull
	@Size(min = 36, max = 36)
	@Id
	@Column(name = "id", length = 36, unique = true, nullable = false, updatable = false)
	private String id = UUID.randomUUID().toString();

	@Min(0)
	@Max(Constants.MAX_IV)
	@Column(name = "ivhp", nullable = false, unique = false, updatable = false, insertable = true)
	private int ivHp;

	@Min(0)
	@Max(Constants.MAX_IV)
	@Column(name = "ivmagicalattack", nullable = false, unique = false, updatable = false, insertable = true)
	private int ivMagicalAttack;

	@Min(0)
	@Max(Constants.MAX_IV)
	@Column(name = "ivmagicaldefense", nullable = false, unique = false, updatable = false, insertable = true)
	private int ivMagicalDefense;

	@Min(0)
	@Max(Constants.MAX_IV)
	@Column(name = "ivmp", nullable = false, unique = false, updatable = false, insertable = true)
	private int ivMp;

	@Min(0)
	@Max(Constants.MAX_IV)
	@Column(name = "ivphysicalattack", nullable = false, unique = false, updatable = false, insertable = true)
	private int ivPhysicalAttack;

	@Min(0)
	@Max(Constants.MAX_IV)
	@Column(name = "ivphysicaldefense", nullable = false, unique = false, updatable = false, insertable = true)
	private int ivPhysicalDefense;
	@Min(0)
	@Max(Constants.MAX_IV)
	@Column(name = "ivspeed", nullable = false, unique = false, updatable = false, insertable = true)
	private int ivSpeed;

	@Min(Constants.MIN_LEVEL)
	@Max(Constants.MAX_LEVEL)
	@Column(name = "level", nullable = false, unique = false, updatable = true, insertable = true)
	private int level;

	// TODO [LOW] Make nature an entity, so that they are not fixed at compile time.
	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "nature", nullable = false, unique = false, updatable = false, insertable = true)
	private ENature nature = ENature.SERIOUS;

	@NotEmpty
	@Size(min = 1, max = 255)
	@Column(name = "nickname", nullable = false, length = 255, updatable = true, insertable = true)
	private String nickname = StringUtils.EMPTY;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, targetEntity = Player.class)
	@JoinColumn(name = "player", nullable = false, unique = false, updatable = false, insertable = true, foreignKey = @ForeignKey(name = "fk_charstate_player"))
	private Player player;

	public CharacterState() {
	}

	/**
	 * @return the character
	 */
	public Character getCharacter() {
		return character;
	}

	@Override
	@JsonIgnore
	public EEntityMeta getEntityMeta() {
		return EEntityMeta.CHARACTER_STATE;
	}

	/**
	 * @return the exp
	 */
	public int getExp() {
		return exp;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the ivHp
	 */
	public int getIvHp() {
		return ivHp;
	}

	/**
	 * @return the ivMagicalAttack
	 */
	public int getIvMagicalAttack() {
		return ivMagicalAttack;
	}

	/**
	 * @return the ivMagicalDefense
	 */
	public int getIvMagicalDefense() {
		return ivMagicalDefense;
	}

	/**
	 * @return the ivMp
	 */
	public int getIvMp() {
		return ivMp;
	}

	@JsonProperty
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	@Transient
	public IComputedStatus getComputedStatus() {
		return IComputedStatus.get(this);
	}

	/**
	 * @return the ivPhysicalAttack
	 */
	public int getIvPhysicalAttack() {
		return ivPhysicalAttack;
	}

	/**
	 * @return the ivPhysicalDefense
	 */
	public int getIvPhysicalDefense() {
		return ivPhysicalDefense;
	}

	/**
	 * @return the ivSpeed
	 */
	public int getIvSpeed() {
		return ivSpeed;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return the nature
	 */
	public ENature getNature() {
		return nature;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @return the player
	 */
	@JsonIgnore
	public Player getPlayer() {
		return player;
	}

	@Override
	@JsonIgnore
	public Serializable getPrimaryKey() {
		return id;
	}

	/**
	 * @param ivHp
	 *            the ivHp to set
	 */
	void setIvHp(final int ivHp) {
		this.ivHp = ivHp;
	}

	/**
	 * @param ivMagicalAttack
	 *            the ivMagicalAttack to set
	 */
	void setIvMagicalAttack(final int ivMagicalAttack) {
		this.ivMagicalAttack = ivMagicalAttack;
	}

	/**
	 * @param ivMagicalDefense
	 *            the ivMagicalDefense to set
	 */
	void setIvMagicalDefense(final int ivMagicalDefense) {
		this.ivMagicalDefense = ivMagicalDefense;
	}

	/**
	 * @param ivMp
	 *            the ivMp to set
	 */
	void setIvMp(final int ivMp) {
		this.ivMp = ivMp;
	}

	/**
	 * @param ivPhysicalAttack
	 *            the ivPhysicalAttack to set
	 */
	void setIvPhysicalAttack(final int ivPhysicalAttack) {
		this.ivPhysicalAttack = ivPhysicalAttack;
	}

	/**
	 * @param ivPhysicalDefense
	 *            the ivPhysicalDefense to set
	 */
	void setIvPhysicalDefense(final int ivPhysicalDefense) {
		this.ivPhysicalDefense = ivPhysicalDefense;
	}

	/**
	 * @param ivSpeed
	 *            the ivSpeed to set
	 */
	void setIvSpeed(final int ivSpeed) {
		this.ivSpeed = ivSpeed;
	}

	@SuppressWarnings("boxing")
	@Override
	public String toString() {
		return String.format("CharacterState(id=%s,level=%d,nature=%s,exp=%d)", id, level, nature, exp);
	}

	@Override
	void forEachAssociatedObject(final Consumer<IAccessible<AbstractStoredEntity>> consumer) {
		consumer.accept(new IAccessible<AbstractStoredEntity>() {
			@Override
			public AbstractStoredEntity get() {
				return getPlayer();
			}

			@Override
			public void set(final AbstractStoredEntity replacement) {
				setPlayer((Player) replacement);
			}
		});
		consumer.accept(new IAccessible<AbstractStoredEntity>() {
			@Override
			public AbstractStoredEntity get() {
				return getCharacter();
			}

			@Override
			public void set(final AbstractStoredEntity replacement) {
				setCharacter((Character) replacement);
			}
		});
	}

	/**
	 * @param character
	 *            the character to set
	 */
	void setCharacter(final Character character) {
		this.character = character;
	}

	/**
	 * @param exp
	 *            the exp to set
	 */
	void setExp(final int exp) {
		this.exp = exp;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	void setId(@NonNull final String id) {
		this.id = id;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	void setLevel(final int level) {
		this.level = level;
	}

	/**
	 * @param nature
	 *            the nature to set
	 */
	void setNature(final ENature nature) {
		this.nature = nature;
	}

	/**
	 * @param nickname
	 *            the nickname to set
	 */
	void setNickname(final String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	void setPlayer(final Player player) {
		this.player = player;
	}
}