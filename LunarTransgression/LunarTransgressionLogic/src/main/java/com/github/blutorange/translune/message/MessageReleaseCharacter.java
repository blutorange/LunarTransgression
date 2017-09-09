package com.github.blutorange.translune.message;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;
import com.jsoniter.annotation.JsonProperty;

public class MessageReleaseCharacter implements ILunarPayload {
	String characterState = StringUtils.EMPTY;

	/**
	 * @return the details
	 */
	@JsonProperty(required = false)
	public String getCharacterState() {
		return characterState;
	}

	/**
	 * @param details
	 *            the details to set
	 */
	public void setCharacterState(@Nullable final String characterState) {
		this.characterState = characterState != null ? characterState : StringUtils.EMPTY;
	}

	@Override
	public @NonNull ELunarMessageType messageType() {
		return ELunarMessageType.RELEASE_CHARACTER;
	}
}