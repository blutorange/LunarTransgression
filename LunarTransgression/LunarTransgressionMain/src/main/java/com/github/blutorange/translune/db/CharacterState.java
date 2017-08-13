package com.github.blutorange.translune.db;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

@Entity
@Table(name = "charstate")
public class CharacterState extends AbstractEntity {
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

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(@Nullable final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CharacterState))
			return false;
		final CharacterState other = (CharacterState) obj;
		if (!id.equals(other.id))
			return false;
		return true;
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

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id.hashCode();
		return result;
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
		return String.format("CharacterState(level=%d,maxHp=%d,maxMp=%d,pAttack=%d,pDefense=%d,mAttack=%d,mDefense=%d,speed=%d)", level, maxHp, maxMp, physicalAttack, physicalDefense, magicalAttack, magicalDefense, speed);
	}


}