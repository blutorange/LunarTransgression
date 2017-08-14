package com.github.blutorange.translune.socket;

import org.eclipse.jdt.annotation.Nullable;

import com.jsoniter.annotation.JsonProperty;

public class LunarMessage {
	@JsonProperty(required = true)
	int time;

	@JsonProperty(required = true)
	ELunarMessageType type = ELunarMessageType.UNKNOWN;

	@JsonProperty(required = false)
	ELunarStatusCode status = ELunarStatusCode.OK;

	@JsonProperty(required = false)
	@Nullable
	String payload;

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
	 * @return the id
	 */
	public int getId() {
		return time;
	}



	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * @return the payload
	 */
	@Nullable
	public String getPayload() {
		return payload;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(final int id) {
		this.time = id;
	}
	/**
	 * @param payload the payload to set
	 */

	public void setPayload(@Nullable final String payload) {
		this.payload = payload;
	}
	/**
	 * @return the type
	 */
	public ELunarMessageType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(@Nullable final ELunarMessageType type) {
		this.type = type != null ? type : ELunarMessageType.UNKNOWN;
	}

	/**
	 * @return the status
	 */
	public ELunarStatusCode getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(@Nullable final ELunarStatusCode status) {
		this.status = status != null ? status : ELunarStatusCode.OK;
	}
}