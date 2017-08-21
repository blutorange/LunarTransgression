package com.github.blutorange.translune.logic;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.util.Constants;
import com.github.blutorange.translune.util.MathUtil;

public class BattleStatus {
	private int hp;
	private int mp;
	private int stagePhysicalAttack;
	private int stagePhysicalDefense;
	private int stageMagicalAttack;
	private int stageMagicalDefense;
	private int stageSpeed;
	@Nullable
	private final EStatusCondition statusConditions  = null;
	
	public BattleStatus() {
		hp = Constants.MAX_RELATIVE_HP;
		mp = Constants.MAX_RELATIVE_MP;
	}

	/**
	 * @return the stagePhysicalAttack
	 */
	public int getStagePhysicalAttack() {
		return stagePhysicalAttack;
	}

	/**
	 * @param stagePhysicalAttack the stagePhysicalAttack to set
	 */
	public void setStagePhysicalAttack(final int stagePhysicalAttack) {
		this.stagePhysicalAttack = clampStage(stagePhysicalAttack);
	}

	/**
	 * @param hp the hp to set
	 */
	public void setHp(final int hp) {
		this.hp = MathUtil.clamp(hp, 0 ,Constants.MAX_RELATIVE_HP);
	}

	/**
	 * @param mp the mp to set
	 */
	public void setMp(final int mp) {
		this.mp = MathUtil.clamp(mp, 0 ,Constants.MAX_RELATIVE_MP);
	}

	/**
	 * @return the stagePhysicalDefense
	 */
	public int getStagePhysicalDefense() {
		return stagePhysicalDefense;
	}

	/**
	 * @param stagePhysicalDefense the stagePhysicalDefense to set
	 */
	public void setStagePhysicalDefense(final int stagePhysicalDefense) {
		this.stagePhysicalDefense = clampStage(stagePhysicalDefense);
	}

	/**
	 * @return the stageMagicalAttack
	 */
	public int getStageMagicalAttack() {
		return stageMagicalAttack;
	}

	/**
	 * @param stageMagicalAttack the stageMagicalAttack to set
	 */
	public void setStageMagicalAttack(final int stageMagicalAttack) {
		this.stageMagicalAttack = clampStage(stageMagicalAttack);
	}

	/**
	 * @return the stageMagicalDefense
	 */
	public int getStageMagicalDefense() {
		return stageMagicalDefense;
	}

	/**
	 * @param stageMagicalDefense the stageMagicalDefense to set
	 */
	public void setStageMagicalDefense(final int stageMagicalDefense) {
		this.stageMagicalDefense = clampStage(stageMagicalDefense);
	}

	/**
	 * @return the stageSpeed
	 */
	public int getStageSpeed() {
		return stageSpeed;
	}

	/**
	 * @param stageSpeed the stageSpeed to set
	 */
	public void setStageSpeed(final int stageSpeed) {
		this.stageSpeed = clampStage(stageSpeed);
	}

	/**
	 * @return the hp
	 */
	public int getHp() {
		return hp;
	}

	/**
	 * @return the mp
	 */
	public int getMp() {
		return mp;
	}

	/**
	 * @return the statusConditions
	 */
	@Nullable
	public EStatusCondition getStatusConditions() {
		return statusConditions;
	}
	
	public void changeStagePhysicalAttack(final int amount) {
		stagePhysicalAttack = clampStage(stagePhysicalAttack+amount);
	}

	public void changeStagePhysicalDefense(final int amount) {
		stagePhysicalDefense = clampStage(stagePhysicalDefense+amount);
	}

	public void changeStageMagicalAttack(final int amount) {
		stageMagicalAttack = clampStage(stageMagicalAttack+amount);
	}

	public void changeStageMagicalDefense(final int amount) {
		stageMagicalDefense = clampStage(stageMagicalDefense+amount);
	}

	public void changeStageSpeed(final int amount) {
		stageSpeed = clampStage(stageSpeed+amount);
	}

	private int clampStage(final int stage) {
		return MathUtil.clamp(stage, Constants.MIN_STAGE, Constants.MAX_STAGE);
	}
}