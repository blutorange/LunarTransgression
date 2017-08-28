package com.github.blutorange.translune.message;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.LunarMessage;
import com.jsoniter.annotation.JsonProperty;

public class MessageInviteAcceptResponse extends AMessageMessageResponse {
	String nickname = StringUtils.EMPTY;

	@Deprecated
	public MessageInviteAcceptResponse() {
	}

	public MessageInviteAcceptResponse(final int origin, final String nickname, final String message) {
		super(origin, message);
		this.nickname = nickname;
	}

	public MessageInviteAcceptResponse(final LunarMessage requestMessage, final String nickname, final String message) {
		this(requestMessage.getTime(), message, nickname);
	}

	/**
	 * @return the nickname
	 */
	@JsonProperty(required = true)
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
	public ELunarMessageType messageType() {
		return ELunarMessageType.INVITE_ACCEPT_RESPONSE;
	}
}