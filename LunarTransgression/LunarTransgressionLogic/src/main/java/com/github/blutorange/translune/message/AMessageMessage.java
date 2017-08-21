package com.github.blutorange.translune.message;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ILunarPayload;
import com.jsoniter.annotation.JsonProperty;

abstract class AMessageMessage implements ILunarPayload {
	@JsonProperty(required = true)
	String message = StringUtils.EMPTY;

	public AMessageMessage() {
	}

	public AMessageMessage(final String message) {
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(@Nullable final String message) {
		this.message = message != null ? message : StringUtils.EMPTY;
	}
}