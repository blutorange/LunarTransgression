package com.github.blutorange.translune.socket;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.logic.EBattleCommandType;
import com.jsoniter.annotation.JsonProperty;

public class BattleCommand {
	@Nullable
	private String action;

	private String[] targets;

	private EBattleCommandType type;

	@Deprecated
	public BattleCommand() {
		type = EBattleCommandType.DEFEND;
		targets = ArrayUtils.EMPTY_STRING_ARRAY;
	}

	/**
	 * @return the action
	 */

	@Nullable
	@JsonProperty(required = false, nullable = true)
	public String getAction() {
		return action;
	}

	/**
	 * @return the target
	 */
	@JsonProperty(required = true, collectionValueNullable = false, nullable = false)
	public String[] getTargets() {
		return targets;
	}

	/**
	 * @return the type
	 */
	@JsonProperty(required = true, nullable = false)
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
	public void setTargets(final String@Nullable[] targets) {
		this.targets = targets != null ? targets : ArrayUtils.EMPTY_STRING_ARRAY;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(@Nullable final EBattleCommandType type) {
		this.type = type != null ? type : EBattleCommandType.DEFEND;
	}
}