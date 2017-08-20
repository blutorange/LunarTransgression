package com.github.blutorange.translune.socket.message;

import com.github.blutorange.translune.socket.ILunarPayload;
import com.github.blutorange.translune.socket.IMessageResponse;
import com.jsoniter.annotation.JsonIgnore;

abstract class AMessageResponse implements ILunarPayload, IMessageResponse {
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	int origin;

	public AMessageResponse() {
	}

	public AMessageResponse(final int origin) {
		this.origin = origin;
	}

	/**
	 * @return the origin
	 */
	@Override
	public int getOrigin() {
		return origin;
	}

	/**
	 * @param origin
	 *            the origin to set
	 */
	@Override
	public void setOrigin(final int origin) {
		this.origin = origin;
	}
}