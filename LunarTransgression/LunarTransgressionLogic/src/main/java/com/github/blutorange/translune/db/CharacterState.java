package com.github.blutorange.translune.db;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "charstate")
public class CharacterState extends AbstractEntity {
	@NotNull
	@OneToOne(targetEntity = Character.class, cascade = CascadeType.ALL, orphanRemoval = true, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "character", updatable = false, insertable = true, nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_char_cstate"))
	private Character character;

	@Min(0)
	@Max(9999)
	@Column(name = "exp", nullable = false, unique = false)
	private int exp;
	@Min(0)
	@Max(999)
	@Column(name = "hp", nullable = false, unique = false)
	private int hp;

	@NotNull
	@Size(min = 36, max = 36)
	@Id
	@Column(name = "id", length = 36, unique = true, nullable = false)
	private String id = UUID.randomUUID().toString();

	@Min(1)
	@Max(20)
	@Column(name = "level", nullable = false, unique = false)
	private int level;

	@Min(0)
	@Max(99)
	@Column(name = "magicalattack", nullable = false, unique = false)
	private int magicalAttack;

	@Min(0)
	@Max(99)
	@Column(name = "magicaldefense", nullable = false, unique = false)
	private int magicalDefense;

	@Min(0)
	@Max(999)
	@Column(name = "maxhp", nullable = false, unique = false)
	private int maxHp;

	@Min(0)
	@Max(999)
	@Column(name = "maxmp", nullable = false, unique = false)
	private int maxMp;

	@Min(0)
	@Max(999)
	@Column(name = "mp", nullable = false, unique = false)
	private int mp;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "nickname", nullable = false, length = 255)
	private String nickname = StringUtils.EMPTY;

	@Min(0)
	@Max(99)
	@Column(name = "physicalattack", nullable = false, unique = false)
	private int physicalAttack;

	@Min(0)
	@Max(99)
	@Column(name = "physicaldefense", nullable = false, unique = false)
	private int physicalDefense;

	@Min(0)
	@Max(99)
	@Column(name = "speed", nullable = false, unique = false)
	private int speed;

	/**
	 * @return the character
	 */
	public Character getCharacter() {
		return character;
	}

	@Override
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
	 * @return the hp
	 */
	public int getHp() {
		return hp;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return the magicalAttack
	 */
	public int getMagicalAttack() {
		return magicalAttack;
	}

	/**
	 * @return the magicalDefense
	 */
	public int getMagicalDefense() {
		return magicalDefense;
	}

	/**
	 * @return the maxHp
	 */
	public int getMaxHp() {
		return maxHp;
	}

	/**
	 * @return the maxMp
	 */
	public int getMaxMp() {
		return maxMp;
	}

	/**
	 * @return the mp
	 */
	public int getMp() {
		return mp;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @return the physicalAttack
	 */
	public int getPhysicalAttack() {
		return physicalAttack;
	}

	/**
	 * @return the physicalDefense
	 */
	public int getPhysicalDefense() {
		return physicalDefense;
	}

	@Override
	public Serializable getPrimaryKey() {
		return id;
	}

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * @param character the character to set
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
	 * @param hp
	 *            the hp to set
	 */
	void setHp(final int hp) {
		this.hp = hp;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	void setId(final String id) {
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
	 * @param magicalAttack
	 *            the magicalAttack to set
	 */
	void setMagicalAttack(final int magicalAttack) {
		this.magicalAttack = magicalAttack;
	}

	/**
	 * @param magicalDefense
	 *            the magicalDefense to set
	 */
	void setMagicalDefense(final int magicalDefense) {
		this.magicalDefense = magicalDefense;
	}

	/**
	 * @param maxHp
	 *            the maxHp to set
	 */
	void setMaxHp(final int maxHp) {
		this.maxHp = maxHp;
	}

	/**
	 * @param maxMp
	 *            the maxMp to set
	 */
	void setMaxMp(final int maxMp) {
		this.maxMp = maxMp;
	}

	/**
	 * @param mp
	 *            the mp to set
	 */
	void setMp(final int mp) {
		this.mp = mp;
	}

	/**
	 * @param nickname
	 *            the nickname to set
	 */
	void setNickname(final String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @param physicalAttack
	 *            the physicalAttack to set
	 */
	void setPhysicalAttack(final int physicalAttack) {
		this.physicalAttack = physicalAttack;
	}

	/**
	 * @param physicalDefense
	 *            the physicalDefense to set
	 */
	void setPhysicalDefense(final int physicalDefense) {
		this.physicalDefense = physicalDefense;
	}

	/**
	 * @param speed
	 *            the speed to set
	 */
	void setSpeed(final int speed) {
		this.speed = speed;
	}

	@SuppressWarnings("boxing")
	@Override
	public String toString() {
		return String.format(
				"CharacterState(level=%d,maxHp=%d,maxMp=%d,pAttack=%d,pDefense=%d,mAttack=%d,mDefense=%d,speed=%d)",
				level, maxHp, maxMp, physicalAttack, physicalDefense, magicalAttack, magicalDefense, speed);
	}
}