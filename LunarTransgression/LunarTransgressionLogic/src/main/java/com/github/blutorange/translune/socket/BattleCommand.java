package com.github.blutorange.translune.socket;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.logic.EBattleCommandType;
import com.jsoniter.annotation.JsonProperty;

public class BattleCommand {
	@Nullable
	@JsonProperty(required = false, nullable = true)
	private String action;

	@JsonProperty(required = true, collectionValueNullable = false, nullable = false)
	private String[] targets = ArrayUtils.EMPTY_STRING_ARRAY;

	@JsonProperty(required = true, nullable = false)
	private EBattleCommandType type = EBattleCommandType.DEFEND;

	/**
	 * @return the action
	 */

	@Nullable
	public String getAction() {
		return action;
	}

	/**
	 * @return the target
	 */

	public String[] getTargets() {
		return targets;
	}

	/**
	 * @return the type
	 */

	public EBattleCommandType getType() {
		return type;
	}

	/**
	 * @param action
	 *            the action to set
	 */
	public void setAction(@Nullable final String action) {
		this.action = action;
	}

	/**
	 * @param targets
	 *            the target to set
	 */
	public void setTargets(final String[] targets) {
		this.targets = targets;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(final EBattleCommandType type) {
		this.type = type;
	}
}