package com.github.blutorange.translune.logic;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.CharacterState;
import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.output.JsonStream;

public interface IComputedBattleStatus extends IComputedStatus {
	public final static String JSON_ACCURACY = "accuracy";
	public final static String JSON_EVASION = "evasion";
	public final static String JSON_MAGICAL_ATTACK = "magicalAttack";
	public final static String JSON_MAGICAL_DEFENSE = "magicalDefense";
	public final static String JSON_MAX_HP = "maxHp";
	public final static String JSON_MAX_MP = "maxMp";
	public final static String JSON_PHYSICAL_ATTACK = "physicalAttack";
	public final static String JSON_PHYSICAL_DEFENSE = "physicalAttack";
	public final static String JSON_SPEED = "speed";
	static void encodeJson(@Nullable final Object object, final JsonStream stream) throws IOException {
		if (object == null) {
			stream.writeNull();
			return;
		}
		final IComputedBattleStatus s = (IComputedBattleStatus)object;
		stream.writeObjectStart();

		stream.writeObjectField(JSON_ACCURACY);
		stream.writeVal(s.getComputedBattleAccuracy());
		stream.writeMore();

		stream.writeObjectField(JSON_EVASION);
		stream.writeVal(s.getComputedBattleEvasion());
		stream.writeMore();

		stream.writeObjectField(JSON_MAGICAL_ATTACK);
		stream.writeVal(s.getComputedBattleMagicalAttack());
		stream.writeMore();

		stream.writeObjectField(JSON_MAGICAL_DEFENSE);
		stream.writeVal(s.getComputedBattleMagicalDefense());
		stream.writeMore();

		stream.writeObjectField(JSON_MAX_HP);
		stream.writeVal(s.getComputedBattleMaxHp());
		stream.writeMore();

		stream.writeObjectField(JSON_MAX_MP);
		stream.writeVal(s.getComputedBattleMaxMp());
		stream.writeMore();

		stream.writeObjectField(JSON_PHYSICAL_ATTACK);
		stream.writeVal(s.getComputedBattlePhysicalAttack());
		stream.writeMore();

		stream.writeObjectField(JSON_PHYSICAL_DEFENSE);
		stream.writeVal(s.getComputedBattlePhysicalDefense());
		stream.writeMore();

		stream.writeObjectField(JSON_SPEED);
		stream.writeVal(s.getComputedBattleSpeed());
		stream.writeMore();

		stream.writeObjectEnd();
	}
	static IComputedBattleStatus get(final CharacterState characterState, final BattleStatus battleStatus) {
		return new ComputedBattleStatus(characterState, battleStatus);
	}
	@JsonIgnore
	BattleStatus getBattleStatus();

	int getComputedBattleAccuracy();
	int getComputedBattleEvasion();
	/** @return The current absolute HP. */
	int getComputedBattleHpAbsolute();
	int getComputedBattleMagicalAttack();

	int getComputedBattleMagicalDefense();

	/** @return The maximum HP. */
	int getComputedBattleMaxHp();
	/** @return The maximum MP. */
	int getComputedBattleMaxMp();
	/** @return The current absolute MP. */
	int getComputedBattleMpAbsolute();
	int getComputedBattlePhysicalAttack();
	int getComputedBattlePhysicalDefense();
	int getComputedBattleSpeed();
	@JsonIgnore
	@Override
	IComputedBattleStatus getSnapshot();
	/**
	 * @param amountAbsolute Positive for healing, negative for damage.
	 */
	void modifyHp(int amountAbsolute);
	/**
	 * @param amountAbsolute Positive for healing, negative for damage.
	 */
	void modifyMp(int amountAbsolute);

	void setHpAbsolute(int absoluteHp);

	void setMpAbsolute(int absoluteMp);
}