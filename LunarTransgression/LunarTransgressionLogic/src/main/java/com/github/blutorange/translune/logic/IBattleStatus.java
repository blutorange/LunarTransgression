package com.github.blutorange.translune.logic;

import java.io.IOException;

import org.apache.commons.math3.fraction.BigFraction;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.common.ICopyable;
import com.github.blutorange.translune.util.Constants;
import com.jsoniter.output.JsonStream;

public interface IBattleStatus extends ICopyable<IBattleStatus> {
	String JSON_ACCURACY = "accuracy";
	String JSON_EVASION = "evasion";
	String JSON_MAGICAL_ATTACK = "magicalAttack";
	String JSON_MAGICAL_DEFENSE = "magicalDefense";
	String JSON_HP = "hp";
	String JSON_MP = "mp";
	String JSON_PHYSICAL_ATTACK = "physicalAttack";
	String JSON_PHYSICAL_DEFENSE = "physicalDefense";
	String JSON_STATUS_CONDITION = "statusCondition";
	String JSON_SPEED = "speed";

	void changeStageAccuracy(int amount);

	void changeStageEvasion(int amount);

	void changeStageMagicalAttack(int amount);

	void changeStageMagicalDefense(int amount);

	void changeStagePhysicalAttack(int amount);

	void changeStagePhysicalDefense(int amount);

	void changeStageSpeed(int amount);

	/**
	 * @return The current HP, relative. The denominator is {@link Constants#MAX_RELATIVE_HP}
	 */
	int getHp();

	/**
	 * @return The current MP, relative. The denominator is {@link Constants#MAX_RELATIVE_MP}
	 */
	int getMp();

	/**
	 * @return the stageAccuracy
	 */
	int getStageAccuracy();

	/**
	 * @return the stageEvasion
	 */
	int getStageEvasion();

	/**
	 * @return the stageMagicalAttack
	 */
	int getStageMagicalAttack();

	/**
	 * @return the stageMagicalDefense
	 */
	int getStageMagicalDefense();

	/**
	 * @return the stagePhysicalAttack
	 */
	int getStagePhysicalAttack();

	/**
	 * @return the stagePhysicalDefense
	 */
	int getStagePhysicalDefense();

	/**
	 * @return the stageSpeed
	 */
	int getStageSpeed();

	/**
	 * @return the statusConditions
	 */
	@Nullable
	EStatusCondition getStatusCondition();

	/**
	 * @param hp
	 *            the hp to set
	 */
	void setHp(int hp);

	/**
	 * @param mp
	 *            the mp to set
	 */
	void setMp(int mp);

	/**
	 * @param stageAccuracy
	 *            the stageAccuracy to set
	 */
	void setStageAccuracy(int stageAccuracy);

	/**
	 * @param stageEvasion
	 *            the stageEvasion to set
	 */
	void setStageEvasion(int stageEvasion);

	/**
	 * @param stageMagicalAttack
	 *            the stageMagicalAttack to set
	 */
	void setStageMagicalAttack(int stageMagicalAttack);

	/**
	 * @param stageMagicalDefense
	 *            the stageMagicalDefense to set
	 */
	void setStageMagicalDefense(int stageMagicalDefense);

	/**
	 * @param stagePhysicalAttack
	 *            the stagePhysicalAttack to set
	 */
	void setStagePhysicalAttack(int stagePhysicalAttack);

	/**
	 * @param stagePhysicalDefense
	 *            the stagePhysicalDefense to set
	 */
	void setStagePhysicalDefense(int stagePhysicalDefense);

	/**
	 * @param stageSpeed
	 *            the stageSpeed to set
	 */
	void setStageSpeed(int stageSpeed);

	/**
	 * @param statusConditions
	 *            the statusConditions to set
	 */
	void setStatusCondition(EStatusCondition statusConditions);

	BigFraction getHpFraction();

	BigFraction getMpFraction();

	default void writeJson(final JsonStream stream) throws IOException {
		stream.writeObjectStart();

		stream.writeObjectField(JSON_ACCURACY);
		stream.writeVal(getStageAccuracy());
		stream.writeMore();

		stream.writeObjectField(JSON_EVASION);
		stream.writeVal(getStageEvasion());
		stream.writeMore();

		stream.writeObjectField(JSON_MAGICAL_ATTACK);
		stream.writeVal(getStageMagicalAttack());
		stream.writeMore();

		stream.writeObjectField(JSON_MAGICAL_DEFENSE);
		stream.writeVal(getStageMagicalDefense());
		stream.writeMore();

		stream.writeObjectField(JSON_HP);
		stream.writeVal(getHp());
		stream.writeMore();

		stream.writeObjectField(JSON_MP);
		stream.writeVal(getMp());
		stream.writeMore();

		stream.writeObjectField(JSON_PHYSICAL_ATTACK);
		stream.writeVal(getStagePhysicalAttack());
		stream.writeMore();

		stream.writeObjectField(JSON_PHYSICAL_DEFENSE);
		stream.writeVal(getStagePhysicalDefense());
		stream.writeMore();

		stream.writeObjectField(JSON_STATUS_CONDITION);
		stream.writeVal(getStatusCondition());
		stream.writeMore();

		stream.writeObjectField(JSON_SPEED);
		stream.writeVal(getStageSpeed());

		stream.writeObjectEnd();
	}

	static void encodeJson(@Nullable final Object object, final JsonStream stream) throws IOException {
		if (object == null) {
			stream.writeNull();
			return;
		}
		final IBattleStatus s = (IBattleStatus) object;
		s.writeJson(stream);
	}
}