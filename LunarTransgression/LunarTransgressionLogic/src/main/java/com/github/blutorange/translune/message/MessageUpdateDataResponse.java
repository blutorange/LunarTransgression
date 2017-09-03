package com.github.blutorange.translune.message;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.serial.JsoniterCollections;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.LunarMessage;
import com.jsoniter.annotation.JsonIgnore;

public class MessageUpdateDataResponse extends AMessageResponse {
	private Object data;

	@Deprecated
	public MessageUpdateDataResponse() {
		data = JsoniterCollections.emptyMap();
	}

	public MessageUpdateDataResponse(final int origin) {
		this(origin, JsoniterCollections.emptyMap());
	}

	public MessageUpdateDataResponse(final int origin, final Object data) {
		super(origin);
		this.data = data;
	}

	public MessageUpdateDataResponse(final LunarMessage message) {
		this(message, JsoniterCollections.emptyMap());
	}

	public MessageUpdateDataResponse(final LunarMessage requestMessage, final Object data) {
		this(requestMessage.getTime(), data);
	}

	/**
	 * @return the data
	 */
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public Object getData() {
		return data;
	}

	@Override
	public ELunarMessageType messageType() {
		return ELunarMessageType.UPDATE_DATA_RESPONSE;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(@Nullable final Object data) {
		this.data = data != null ? data : JsoniterCollections.emptyMap();
	}
}