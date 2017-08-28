package com.github.blutorange.translune.message;

import com.github.blutorange.translune.socket.ILunarPayload;
import com.github.blutorange.translune.socket.IMessageResponse;
import com.jsoniter.annotation.JsonIgnore;

abstract class AMessageMessageResponse extends AMessageMessage implements ILunarPayload, IMessageResponse {
	int origin;

	public AMessageMessageResponse() {
	}

	public AMessageMessageResponse(final int origin, final String message) {
		super(message);
		this.origin = origin;
	}

	/**
	 * @return the origin
	 */
	@Override
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
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