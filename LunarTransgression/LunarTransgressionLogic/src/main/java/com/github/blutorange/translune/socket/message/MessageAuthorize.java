package com.github.blutorange.translune.socket.message;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarMessage;
import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;

public class MessageAuthorize implements ILunarMessage {
	@JsonProperty(required = true)
	String nickname = StringUtils.EMPTY;

	@JsonProperty(required = true)
	@JsonIgnore(ignoreDecoding = false, ignoreEncoding = true)
	String initId = StringUtils.EMPTY;

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}
	/**
	 * @return the initId
	 */
	public String getInitId() {
		return initId;
	}
	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(@Nullable final String nickname) {
		this.nickname = nickname != null ? nickname : StringUtils.EMPTY;
	}
	/**
	 * @param initId the initId to set
	 */
	public void setInitId(@org.eclipse.jdt.annotation.Nullable final String initId) {
		this.initId = initId != null ? initId : StringUtils.EMPTY;
	}
	@Override
	public ELunarMessageType getMessageType() {
		return ELunarMessageType.AUTHORIZE;
	}
}