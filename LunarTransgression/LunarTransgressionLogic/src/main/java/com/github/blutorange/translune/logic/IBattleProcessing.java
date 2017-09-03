package com.github.blutorange.translune.logic;

import java.io.IOException;
import java.util.List;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;
import com.github.blutorange.translune.socket.BattleResult;

public interface IBattleProcessing {
	/**
	 *
	 * @param characterStates
	 * @return <0 iff there is no winner yet, 0 iff it is player 1, 1 iff it is
	 *         player 2.
	 */
	int checkBattleEnd(BattleStatus[][] characterStates);

	IDamageResult[] computeDamage(ISkilled skillData, IComputedBattleStatus attacker,
			IComputedBattleStatus... defenders);

	void dealDamage(IDamageResult damageResult, IComputedBattleStatus target, List<String> messages);

	BattleResult[][] distributeExperience(String[] players, List<String[]> characterStates,
			BattleStatus[][] battleStatus, int turn) throws IOException;

	IComputedBattleStatus getComputedStatus(CharacterState characterState, BattleStatus battleStatus);

	IComputedBattleStatus getComputedStatus(String characterState, BattleStatus status) throws IOException;

	IComputedBattleStatus[] getTargetsAlive(ITargettable targettable, IBattleContext context,
			BattleCommand battleCommand, int player, int character);

	void handleError(IBattleContext context, CharacterState user, String... messages);

	void inflictCondition(IStatusConditioned conditionData, IComputedBattleStatus target, List<String> messages,
			IBattleContext context);

	void makeFlinch(IFlinched flinchData, IComputedBattleStatus target, List<String> messages, IBattleContext context);

	boolean moveHits(IAccuracied accuracyData, IComputedBattleStatus user, IComputedBattleStatus target,
			List<String> messages);

	/**
	 *
	 * @param commands
	 * @param players
	 * @param characterStates
	 * @param items
	 * @param effectorStack
	 * @return IBattleResult[playerIndex=0..1][characterIndex=0...3]
	 * @throws IOException
	 */
	BattleAction[][] simulateBattleStep(List<BattleCommand[]> commands, String[] players,
			List<String[]> characterStates, List<String[]> items, BattleStatus[][] battleStatus,
			List<IGlobalBattleEffector> effectorStack, int turn) throws IOException;

	void changeStages(IStaged skill, IComputedBattleStatus target, List<String> messages, IBattleContext context);

	void performHeal(IHealing healData, IComputedBattleStatus user, List<String> messages, IBattleContext context);
}