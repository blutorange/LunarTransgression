package com.github.blutorange.translune.message;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;

public class MessageInviteRetract implements ILunarPayload {
	private String nickname = StringUtils.EMPTY;

	public MessageInviteRetract() {
	}

	public MessageInviteRetract(final String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return the nickname
	 */
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
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.INVITE_RETRACT;
	}
}