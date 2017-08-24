package com.github.blutorange.translune.socket;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.CharacterState;
import com.jsoniter.annotation.JsonProperty;

/**
 * Each battle action takes one press of the message advance button.
 *
 * @author madgaksha
 *
 */
public class BattleAction {
	/**
	 * @return 0 iff the battle continues, 1 iff this command makes the player
	 *         win, -1 iff it makes the player lose.
	 */
	@JsonProperty(required = true)
	private int causesEnd;

	/**
	 * @return Message(s) describing the battle action, eg.
	 *         <code>Chimera uses Slash on Phyrant!</code>.
	 */
	@JsonProperty(required = true)
	private String[] sentences;

	/**
	 * @return {@link CharacterState#getPrimaryKey()} of the characters affected
	 *         by this action.
	 */
	@JsonProperty(required = true)
	private String[] targets;

	/**
	 * @return {@link CharacterState#getPrimaryKey()} of the character that
	 *         initiated the battle action.
	 */
	@JsonProperty(required = true)
	private String user;

	@Deprecated
	public BattleAction() {
		sentences = new String[0];
		targets = new String[0];
		user = StringUtils.EMPTY;
	}

	public BattleAction(final String user, final String[] targets, final String... sentences) {
		this.sentences = sentences;
		this.user = user;
		this.targets = targets;
	}

	public BattleAction(final String character, final String target, final String... sentences) {
		this(character, new String[]{target}, sentences);
	}

	public int causesEnd() {
		return causesEnd;
	}

	public boolean causesLose() {
		return causesEnd < 0;
	}

	public boolean causesVictory() {
		return causesEnd > 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.github.blutorange.translune.logic.IBattleAction#getSentences()
	 */
	public String[] getSentences() {
		return sentences;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.github.blutorange.translune.logic.IBattleAction#getTargets()
	 */
	public String[] getTargets() {
		return targets;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.github.blutorange.translune.logic.IBattleAction#getUser()
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param sentences
	 *            the sentences to set
	 */
	public void setSentences(final String @Nullable [] sentences) {
		this.sentences = sentences != null ? sentences : new String[0];
	}

	/**
	 * @param targets
	 *            the targets to set
	 */
	public void setTargets(final String @Nullable [] targets) {
		this.targets = targets != null ? targets : new String[0];
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(@Nullable final String user) {
		this.user = user != null ? user : StringUtils.EMPTY;
	}

	/**
	 * @param causesEnd the causesEnd to set
	 */
	public void setCausesEnd(final int causesEnd) {
		this.causesEnd = causesEnd;
	}
}