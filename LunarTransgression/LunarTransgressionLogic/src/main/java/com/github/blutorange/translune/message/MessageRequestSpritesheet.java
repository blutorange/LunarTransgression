package com.github.blutorange.translune.message;

import org.apache.commons.lang3.ArrayUtils;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;
import com.jsoniter.annotation.JsonProperty;

public class MessageRequestSpritesheet implements ILunarPayload {
	private String[] resourceNames = ArrayUtils.EMPTY_STRING_ARRAY;
	
	@Deprecated
	public MessageRequestSpritesheet() {
		
	}
	
	public MessageRequestSpritesheet(String... resourceNames) {
		this.resourceNames = resourceNames;
	}
	
	@JsonProperty(required = true, collectionValueNullable = false)
	public String[] getResourceNames() {
		return resourceNames;
	}

	public void setResourceNames(String[] resourceNames) {
		this.resourceNames = resourceNames != null ? resourceNames : ArrayUtils.EMPTY_STRING_ARRAY;
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.REQUEST_SPRITESHEET;
	}
}