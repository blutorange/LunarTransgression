package com.github.blutorange.translune.socket.message;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarMessage;
import com.jsoniter.annotation.JsonProperty;

public class MessageInviteAccepted implements ILunarMessage {
	@JsonProperty(required = true)
	String nickname = StringUtils.EMPTY;

	@Deprecated
	public MessageInviteAccepted() {
	}

	public MessageInviteAccepted(final String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname
	 *            the nickname to set
	 */
	public void setNickname(@Nullable final String nickname) {
		this.nickname = nickname != null ? nickname : StringUtils.EMPTY;
	}

	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.INVITE_ACCEPTED;
	}
}