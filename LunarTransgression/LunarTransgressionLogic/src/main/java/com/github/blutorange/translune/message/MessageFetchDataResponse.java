package com.github.blutorange.translune.message;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.serial.JsoniterCollections;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.LunarMessage;
import com.jsoniter.annotation.JsonIgnore;

public class MessageFetchDataResponse extends AMessageResponse {
	private Object data;

	@Deprecated
	public MessageFetchDataResponse() {
		data = JsoniterCollections.emptyMap();
	}

	public MessageFetchDataResponse(final int origin, final Object data) {
		super(origin);
		this.data = data;
	}

	public MessageFetchDataResponse(final int origin) {
		this(origin, JsoniterCollections.emptyMap());
	}

	public MessageFetchDataResponse(final LunarMessage requestMessage, final Object data) {
		this(requestMessage.getTime(), data);
	}

	public MessageFetchDataResponse(final LunarMessage message) {
		this(message, JsoniterCollections.emptyMap());
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
		return ELunarMessageType.FETCH_DATA_RESPONSE;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(@Nullable final Object data) {
		this.data = data != null ? data : JsoniterCollections.emptyMap();
	}
}