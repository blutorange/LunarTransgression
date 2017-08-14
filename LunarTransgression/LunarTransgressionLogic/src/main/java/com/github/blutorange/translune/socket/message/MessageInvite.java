package com.github.blutorange.translune.socket.message;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;

public class MessageInvite extends AMessageMessage {
	@JsonProperty(required = true)
	String nickname = StringUtils.EMPTY;

	@JsonIgnore(ignoreDecoding=true, ignoreEncoding=false)
	long created = System.currentTimeMillis();

	@Deprecated
	public MessageInvite() {
	}

	public MessageInvite(final String nickname, final String message) {
		super(message);
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
		return ELunarMessageType.INVITE;
	}

	/**
	 * @return the created
	 */
	public long getCreated() {
		return created;
	}
}
