package com.github.blutorange.translune.logic;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.common.MathUtil;
import com.github.blutorange.translune.util.Constants;

public class BattleStatus {
	private int hp;
	private int mp;
	private int stageAccuracy;
	private int stageEvasion;
	private int stageMagicalAttack;
	private int stageMagicalDefense;
	private int stagePhysicalAttack;
	private int stagePhysicalDefense;
	private int stageSpeed;
	@Nullable
	private EStatusCondition statusConditions = null;

	public BattleStatus() {
		hp = Constants.MAX_RELATIVE_HP;
		mp = Constants.MAX_RELATIVE_MP;
	}

	public void changeStageAccuracy(final int amount) {
		stageAccuracy += amount;
	}

	public void changeStageEvasion(final int amount) {
		stageEvasion += amount;
	}

	public void changeStageMagicalAttack(final int amount) {
		stageMagicalAttack += amount;
	}

	public void changeStageMagicalDefense(final int amount) {
		stageMagicalDefense += amount;
	}

	public void changeStagePhysicalAttack(final int amount) {
		stagePhysicalAttack += amount;
	}

	public void changeStagePhysicalDefense(final int amount) {
		stagePhysicalDefense += amount;
	}

	public void changeStageSpeed(final int amount) {
		stageSpeed += amount;
	}

	/**
	 * @return The current HP, relative. The denominator is {@link Constants#MAX_RELATIVE_HP}
	 */
	public int getHp() {
		return hp;
	}

	/**
	 * @return The current MP, relative. The denominator is {@link Constants#MAX_RELATIVE_MP}
	 */
	public int getMp() {
		return mp;
	}

	/**
	 * @return the stageAccuracy
	 */
	public int getStageAccuracy() {
		return clampStage(stageAccuracy);
	}

	/**
	 * @return the stageEvasion
	 */
	public int getStageEvasion() {
		return clampStage(stageEvasion);
	}

	/**
	 * @return the stageMagicalAttack
	 */
	public int getStageMagicalAttack() {
		return clampStage(stageMagicalAttack);
	}

	/**
	 * @return the stageMagicalDefense
	 */
	public int getStageMagicalDefense() {
		return clampStage(stageMagicalDefense);
	}

	/**
	 * @return the stagePhysicalAttack
	 */
	public int getStagePhysicalAttack() {
		return clampStage(stagePhysicalAttack);
	}

	/**
	 * @return the stagePhysicalDefense
	 */
	public int getStagePhysicalDefense() {
		return clampStage(stagePhysicalDefense);
	}

	/**
	 * @return the stageSpeed
	 */
	public int getStageSpeed() {
		return clampStage(stageSpeed);
	}

	/**
	 * @return the statusConditions
	 */
	@Nullable
	public EStatusCondition getStatusConditions() {
		return statusConditions;
	}

	/**
	 * @param hp
	 *            the hp to set
	 */
	public void setHp(final int hp) {
		this.hp = MathUtil.clamp(hp, 0, Constants.MAX_RELATIVE_HP);
	}

	/**
	 * @param mp
	 *            the mp to set
	 */
	public void setMp(final int mp) {
		this.mp = MathUtil.clamp(mp, 0, Constants.MAX_RELATIVE_MP);
	}

	/**
	 * @param stageAccuracy
	 *            the stageAccuracy to set
	 */
	public void setStageAccuracy(final int stageAccuracy) {
		this.stageAccuracy = stageAccuracy;
	}

	/**
	 * @param stageEvasion
	 *            the stageEvasion to set
	 */
	public void setStageEvasion(final int stageEvasion) {
		this.stageEvasion = stageEvasion;
	}

	/**
	 * @param stageMagicalAttack
	 *            the stageMagicalAttack to set
	 */
	public void setStageMagicalAttack(final int stageMagicalAttack) {
		this.stageMagicalAttack = stageMagicalAttack;
	}

	/**
	 * @param stageMagicalDefense
	 *            the stageMagicalDefense to set
	 */
	public void setStageMagicalDefense(final int stageMagicalDefense) {
		this.stageMagicalDefense = stageMagicalDefense;
	}

	/**
	 * @param stagePhysicalAttack
	 *            the stagePhysicalAttack to set
	 */
	public void setStagePhysicalAttack(final int stagePhysicalAttack) {
		this.stagePhysicalAttack = stagePhysicalAttack;
	}

	/**
	 * @param stagePhysicalDefense
	 *            the stagePhysicalDefense to set
	 */
	public void setStagePhysicalDefense(final int stagePhysicalDefense) {
		this.stagePhysicalDefense = stagePhysicalDefense;
	}

	/**
	 * @param stageSpeed
	 *            the stageSpeed to set
	 */
	public void setStageSpeed(final int stageSpeed) {
		this.stageSpeed = stageSpeed;
	}

	/**
	 * @param statusConditions
	 *            the statusConditions to set
	 */
	public void setStatusConditions(final EStatusCondition statusConditions) {
		this.statusConditions = statusConditions;
	}

	private int clampStage(final int stage) {
		return MathUtil.clamp(stage, Constants.MIN_STAGE, Constants.MAX_STAGE);
	}
}