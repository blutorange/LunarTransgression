package com.github.blutorange.translune.message;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;

public class MessageInvite extends AMessageMessage {
	String nickname = StringUtils.EMPTY;

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
		return ELunarMessageType.INVITE;
	}

	/**
	 * @return the created
	 */
	@JsonIgnore(ignoreDecoding=true, ignoreEncoding=false)
	public long getCreated() {
		return created;
	}
}
