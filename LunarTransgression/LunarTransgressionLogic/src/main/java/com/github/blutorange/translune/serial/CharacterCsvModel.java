package com.github.blutorange.translune.serial;

public class CharacterCsvModel {
	private int attack;
	private String cry;
	private int defense;
	private int description;
	private String elements;
	private String experienceGroup;
	private String imgBack;
	private String imgFront;
	private int magicalAttack;
	private int magicalDefense;
	private int maxHp;
	private int maxMp;
	private String name;
	private int speed;

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CharacterCsvModel))
			return false;
		final CharacterCsvModel other = (CharacterCsvModel) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * @return the attack
	 */
	public int getAttack() {
		return attack;
	}

	/**
	 * @return the cry
	 */
	public String getCry() {
		return cry;
	}

	/**
	 * @return the defense
	 */
	public int getDefense() {
		return defense;
	}

	/**
	 * @return the description
	 */
	public int getDescription() {
		return description;
	}

	/**
	 * @return the elements
	 */
	public String getElements() {
		return elements;
	}

	/**
	 * @return the experienceGroup
	 */
	public String getExperienceGroup() {
		return experienceGroup;
	}

	/**
	 * @return the imgBack
	 */
	public String getImgBack() {
		return imgBack;
	}

	/**
	 * @return the imgFront
	 */
	public String getImgFront() {
		return imgFront;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * @param attack
	 *            the attack to set
	 */
	public void setAttack(final int attack) {
		this.attack = attack;
	}

	/**
	 * @param cry
	 *            the cry to set
	 */
	public void setCry(final String cry) {
		this.cry = cry;
	}

	/**
	 * @param defense
	 *            the defense to set
	 */
	public void setDefense(final int defense) {
		this.defense = defense;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final int description) {
		this.description = description;
	}

	/**
	 * @param elements
	 *            the elements to set
	 */
	public void setElements(final String elements) {
		this.elements = elements;
	}

	/**
	 * @param experienceGroup
	 *            the experienceGroup to set
	 */
	public void setExperienceGroup(final String experienceGroup) {
		this.experienceGroup = experienceGroup;
	}

	/**
	 * @param imgBack
	 *            the imgBack to set
	 */
	public void setImgBack(final String imgBack) {
		this.imgBack = imgBack;
	}

	/**
	 * @param imgFront
	 *            the imgFront to set
	 */
	public void setImgFront(final String imgFront) {
		this.imgFront = imgFront;
	}

	/**
	 * @param magicalAttack
	 *            the magicalAttack to set
	 */
	public void setMagicalAttack(final int magicalAttack) {
		this.magicalAttack = magicalAttack;
	}

	/**
	 * @param magicalDefense
	 *            the magicalDefense to set
	 */
	public void setMagicalDefense(final int magicalDefense) {
		this.magicalDefense = magicalDefense;
	}

	/**
	 * @param maxHp
	 *            the maxHp to set
	 */
	public void setMaxHp(final int maxHp) {
		this.maxHp = maxHp;
	}

	/**
	 * @param maxMp
	 *            the maxMp to set
	 */
	public void setMaxMp(final int maxMp) {
		this.maxMp = maxMp;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @param speed
	 *            the speed to set
	 */
	public void setSpeed(final int speed) {
		this.speed = speed;
	}

}
