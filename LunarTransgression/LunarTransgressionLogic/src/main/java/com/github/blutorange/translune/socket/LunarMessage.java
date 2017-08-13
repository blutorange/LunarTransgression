package com.github.blutorange.translune.socket;

import org.eclipse.jdt.annotation.Nullable;

import com.jsoniter.annotation.JsonProperty;

public class LunarMessage {
	@JsonProperty(required = true)
	int id;

	@JsonProperty(required = true)
	ELunarMessageType type = ELunarMessageType.UNKNOWN;

	@JsonProperty(required = false)
	@Nullable
	String payload;

	public LunarMessage() {
	}

	public LunarMessage(final int id, final ELunarMessageType type, final String payload) {
		this.id = id;
		this.type = type;
		this.payload = payload;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
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
		this.id = id;
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
}