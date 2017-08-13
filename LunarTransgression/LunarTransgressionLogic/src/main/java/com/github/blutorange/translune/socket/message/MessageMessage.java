package com.github.blutorange.translune.socket.message;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarMessage;
import com.jsoniter.annotation.JsonProperty;

public class MessageMessage implements ILunarMessage {
	@JsonProperty(required = true)
	EMessageSeverity severity;

	@JsonProperty(required = true)
	String message;

	public MessageMessage() {
		this(EMessageSeverity.INFO, StringUtils.EMPTY);
	}
	public MessageMessage(final EMessageSeverity severity, final String message) {
		this.severity = severity;
		this.message = message;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(@Nullable final String message) {
		this.message = message != null ? message : StringUtils.EMPTY;
	}
	/**
	 * @return the severity
	 */
	public EMessageSeverity getSeverity() {
		return severity;
	}
	/**
	 * @param severity the severity to set
	 */
	public void setSeverity(@Nullable final EMessageSeverity severity) {
		this.severity = severity != null ? severity : EMessageSeverity.WARN;
	}
	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.MESSAGE;
	}
}
