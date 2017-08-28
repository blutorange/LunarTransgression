package com.github.blutorange.translune.message;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;
import com.jsoniter.annotation.JsonProperty;

public class MessageInviteRejected implements ILunarPayload {
	String nickname = StringUtils.EMPTY;

	@Deprecated
	public MessageInviteRejected() {
	}

	public MessageInviteRejected(final String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return the nickname
	 */
	@JsonProperty(required = true)
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(@Nullable final String nickname) {
		this.nickname = nickname != null ? nickname : StringUtils.EMPTY;
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.INVITE_REJECTED;
	}
}