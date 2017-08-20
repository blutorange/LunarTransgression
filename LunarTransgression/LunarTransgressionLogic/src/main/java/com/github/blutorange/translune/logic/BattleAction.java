package com.github.blutorange.translune.logic;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.jsoniter.annotation.JsonProperty;

/**
 * Each battle action takes one press of the message advance button.
 *
 * @author madgaksha
 *
 */
public class BattleAction implements IBattleAction {
	@JsonProperty(required = true)
	private String[] sentences;

	@JsonProperty(required = true)
	private String user;

	@JsonProperty(required = true)
	private String[] targets;

	private int causesEnd;

	@Deprecated
	public BattleAction() {
		sentences = new String[0];
		targets = new String[0];
		user = StringUtils.EMPTY;
	}

	public BattleAction(final String[] sentences, final String user, final String[] targets) {
		this.sentences = sentences;
		this.user = user;
		this.targets = targets;
	}

	/* (non-Javadoc)
	 * @see com.github.blutorange.translune.logic.IBattleAction#getSentences()
	 */
	@Override
	public String[] getSentences() {
		return sentences;
	}

	/* (non-Javadoc)
	 * @see com.github.blutorange.translune.logic.IBattleAction#getUser()
	 */
	@Override
	public String getUser() {
		return user;
	}

	/* (non-Javadoc)
	 * @see com.github.blutorange.translune.logic.IBattleAction#getTargets()
	 */
	@Override
	public String[] getTargets() {
		return targets;
	}

	/**
	 * @param sentences the sentences to set
	 */
	public void setSentences(final String @Nullable[] sentences) {
		this.sentences = sentences != null ? sentences : new String[0];
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(@Nullable final String user) {
		this.user = user != null ? user : StringUtils.EMPTY;
	}

	/**
	 * @param targets the targets to set
	 */
	public void setTargets(final String @Nullable[] targets) {
		this.targets = targets != null ? targets : new String[0];
	}

	@Override
	public int causesEnd() {
		return causesEnd;
	}

	public boolean causesVictory() {
		return causesEnd > 0;
	}

	public boolean causesLose() {
		return causesEnd < 0;
	}
}