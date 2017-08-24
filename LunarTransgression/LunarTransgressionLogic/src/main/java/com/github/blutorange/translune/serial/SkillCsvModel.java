package com.github.blutorange.translune.serial;

public class SkillCsvModel {
	private int accuracy;
	private String description;
	private String effect;
	private String element;
	private boolean highCritical;
	private boolean isPhysical;
	private int mp;
	private String name;
	private int power;
	private int priority;
	private String target;

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
		if (!(obj instanceof SkillCsvModel))
			return false;
		final SkillCsvModel other = (SkillCsvModel) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * @return the accuracy
	 */
	public int getAccuracy() {
		return accuracy;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the effect
	 */
	public String getEffect() {
		return effect;
	}

	/**
	 * @return the element
	 */
	public String getElement() {
		return element;
	}

	/**
	 * @return the mp
	 */
	public int getMp() {
		return mp;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the power
	 */
	public int getPower() {
		return power;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
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
	 * @return the highCritical
	 */
	public boolean isHighCritical() {
		return highCritical;
	}

	/**
	 * @return the isPhysical
	 */
	public boolean isPhysical() {
		return isPhysical;
	}

	/**
	 * @param accuracy
	 *            the accuracy to set
	 */
	public void setAccuracy(final int accuracy) {
		this.accuracy = accuracy;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @param effect
	 *            the effect to set
	 */
	public void setEffect(final String effect) {
		this.effect = effect;
	}

	/**
	 * @param element
	 *            the element to set
	 */
	public void setElement(final String element) {
		this.element = element;
	}

	/**
	 * @param highCritical
	 *            the highCritical to set
	 */
	public void setHighCritical(final boolean highCritical) {
		this.highCritical = highCritical;
	}

	/**
	 * @param mp
	 *            the mp to set
	 */
	public void setMp(final int mp) {
		this.mp = mp;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @param isPhysical
	 *            the isPhysical to set
	 */
	public void setPhysical(final boolean isPhysical) {
		this.isPhysical = isPhysical;
	}

	/**
	 * @param power
	 *            the power to set
	 */
	public void setPower(final int power) {
		this.power = power;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(final int priority) {
		this.priority = priority;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(final String target) {
		this.target = target;
	}

}
