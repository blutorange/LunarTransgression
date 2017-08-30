package com.github.blutorange.translune.message;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.LunarMessage;
import com.jsoniter.annotation.JsonProperty;

public class MessageRequestSpritesheetResponse extends AMessageResponse {
	private String token = StringUtils.EMPTY;
	
	@Deprecated
	public MessageRequestSpritesheetResponse() {
		
	}
	
	public MessageRequestSpritesheetResponse(int origin, String token) {
		super(origin);
		this.token = token;
	}
	
	public MessageRequestSpritesheetResponse(final LunarMessage requestMessage, String token) {
		this(requestMessage.getTime(), token);
	}
	
	@JsonProperty(required = true)
	public String getToken() {
		return token;
	}

	public void setToken(@Nullable String token) {
		this.token = token != null ? token : StringUtils.EMPTY;
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.REQUEST_SPRITESHEET_RESPONSE;
	}
}