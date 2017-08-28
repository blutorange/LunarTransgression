package com.github.blutorange.translune.socket;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;

public class LunarMessage {
	private final long receptionTime = System.currentTimeMillis();

	String payload = StringUtils.EMPTY;

	ELunarStatusCode status = ELunarStatusCode.OK;

	int time;

	ELunarMessageType type = ELunarMessageType.UNKNOWN;

	public LunarMessage() {
	}

	public LunarMessage(final int time, final ELunarMessageType type, final ELunarStatusCode status,
			final String payload) {
		this.time = time;
		this.type = type;
		this.payload = payload;
		this.status = status;
	}

	/**
	 * @return the payload
	 */
	@JsonProperty(required = false)
	public String getPayload() {
		return payload;
	}

	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = true)
	public long getReceptionTime() {
		return receptionTime;
	}

	/**
	 * @return the status
	 */
	@JsonProperty(required = false)
	public ELunarStatusCode getStatus() {
		return status;
	}

	/**
	 * @return the time
	 */
	@JsonProperty(required = true)
	public int getTime() {
		return time;
	}

	/**
	 * @return the type
	 */
	@JsonProperty(required = true)
	public ELunarMessageType getType() {
		return type;
	}

	/**
	 * @param payload
	 *            the payload to set
	 */

	public void setPayload(@Nullable final String payload) {
		this.payload = payload != null ? payload : StringUtils.EMPTY;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(@Nullable final ELunarStatusCode status) {
		this.status = status != null ? status : ELunarStatusCode.OK;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(final int time) {
		this.time = time;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(@Nullable final ELunarMessageType type) {
		this.type = type != null ? type : ELunarMessageType.UNKNOWN;
	}

	@Override
	public String toString() {
		return String.format("LunarMessage(%s,%s,%s)", type, status, time);
	}
}