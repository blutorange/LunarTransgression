package com.github.blutorange.translune.socket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.logic.CharacterStatsDelta;
import com.github.blutorange.translune.logic.IBattleContext;
import com.github.blutorange.translune.logic.IComputedStatus;
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
	private int causesEnd;

	/**
	 * @return Message(s) describing the battle action, eg.
	 *         <code>Chimera uses Slash on Phyrant!</code>.
	 */
	private String[] sentences;

	/**
	 * @return {@link CharacterState#getPrimaryKey()} of the characters affected
	 *         by this action.
	 */
	private String[] targets;

	/**
	 * @return {@link CharacterState#getPrimaryKey()} of the character that
	 *         initiated the battle action.
	 */
	private String user;

	private CharacterStatsDelta[] stateDeltas;

	@Deprecated
	public BattleAction() {
		sentences = new String[0];
		targets = new String[0];
		user = StringUtils.EMPTY;
		stateDeltas = new CharacterStatsDelta[0];
	}

	protected BattleAction(final String user, final String[] targets, final CharacterStatsDelta[] stateDeltas,
			final String[] sentences) {
		this.sentences = sentences;
		this.user = user;
		this.targets = targets;
		this.stateDeltas = stateDeltas;
	}

	public int causesEnd() {
		return causesEnd;
	}

	/**
	 * @return the statusDeltas
	 */
	@JsonProperty(required = true, nullable = false)
	public CharacterStatsDelta[] getStateDeltas() {
		return stateDeltas;
	}

	/**
	 * @param statusDeltas
	 *            the statusDeltas to set
	 */
	public void setStateDeltas(final CharacterStatsDelta @Nullable [] stateDeltas) {
		this.stateDeltas = stateDeltas != null ? stateDeltas : new CharacterStatsDelta[0];
	}

	@JsonProperty(required = true)
	public int getCausesEnd() {
		return causesEnd;
	}

	public boolean causesLose() {
		return causesEnd < 0;
	}

	public boolean causesVictory() {
		return causesEnd > 0;
	}

	@JsonProperty(required = true, collectionValueNullable = false)
	public String[] getSentences() {
		return sentences;
	}

	@JsonProperty(required = true, collectionValueNullable = false)
	public String[] getTargets() {
		return targets;
	}

	@JsonProperty(required = true)
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
	 * @param causesEnd
	 *            the causesEnd to set
	 */
	public void setCausesEnd(final int causesEnd) {
		this.causesEnd = causesEnd;
	}

	public static class Builder implements org.apache.commons.lang3.builder.Builder<BattleAction> {
		List<@NonNull String> sentences = new ArrayList<>();
		String @Nullable [] targets;
		@Nullable
		String character;
		IBattleContext context;

		public Builder(final IBattleContext context) {
			this.context = context;
		}

		public Builder character(final CharacterState character) {
			this.character = character.getId();
			return this;
		}

		public Builder character(final IComputedStatus character) {
			this.character = character.getCharacterState().getId();
			return this;
		}

		public Builder targets(final CharacterState... targets) {
			final String[] newTargets = new String[targets.length];
			for (int i = targets.length; i-- > 0;)
				newTargets[i] = targets[i].getId();
			this.targets = newTargets;
			return this;
		}

		public Builder targets(final IComputedStatus... targets) {
			final String[] newTargets = new String[targets.length];
			for (int i = targets.length; i-- > 0;)
				newTargets[i] = targets[i].getCharacterState().getId();
			this.targets = newTargets;
			return this;
		}

		public Builder addSentences(final @NonNull String... sentences) {
			Collections.addAll(this.sentences, sentences);
			return this;
		}

		public Builder addSentences(final Collection<@NonNull String> sentences) {
			this.sentences.addAll(sentences);
			return this;
		}

		@Override
		public BattleAction build() {
			String[] targets = this.targets;
			final String character = this.character;
			if (targets == null)
				targets = new String[0];
			if (character == null)
				throw new IllegalStateException("character is not set");
			final CharacterStatsDelta[] statDeltas = context.differenceToLast();
			return new BattleAction(character, targets, statDeltas, sentences.toArray(new String[sentences.size()]));
		}

		public List<String> getSentences() {
			return sentences;
		}
	}
}