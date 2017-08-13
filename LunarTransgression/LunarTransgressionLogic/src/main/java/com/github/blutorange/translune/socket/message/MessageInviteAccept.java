package com.github.blutorange.translune.socket.message;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarMessage;
import com.jsoniter.annotation.JsonProperty;

public class MessageInviteAccept implements ILunarMessage {
	@JsonProperty(required = true)
	String nickname = StringUtils.EMPTY;

	@JsonProperty(required = true)
	String message = StringUtils.EMPTY;

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(@Nullable final String nickname) {
		this.nickname = nickname != null ? nickname : StringUtils.EMPTY;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(@Nullable final String message) {
		this.message = message != null ? message : StringUtils.EMPTY;
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.INVITE_ACCEPT;
	}
}
