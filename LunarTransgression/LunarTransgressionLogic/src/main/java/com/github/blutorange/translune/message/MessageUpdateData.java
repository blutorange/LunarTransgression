package com.github.blutorange.translune.message;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.handler.EHandlerUpdateData;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;
import com.jsoniter.annotation.JsonProperty;

public class MessageUpdateData implements ILunarPayload {
	String details = StringUtils.EMPTY;

	EHandlerUpdateData update = EHandlerUpdateData.NONE;

	/**
	 * @return the details
	 */
	@JsonProperty(required = false)
	public String getDetails() {
		return details;
	}

	/**
	 * @return the fetch
	 */
	@JsonProperty(required = true)
	public EHandlerUpdateData getUpdate() {
		return update;
	}

	/**
	 * @param details
	 *            the details to set
	 */
	public void setDetails(@Nullable final String details) {
		this.details = details != null ? details : StringUtils.EMPTY;
	}

	/**
	 * @param fetch
	 *            the fetch to set
	 */
	public void setUpdate(@Nullable final EHandlerUpdateData update) {
		this.update = update != null ? update : EHandlerUpdateData.NONE;
	}

	@Override
	public @NonNull ELunarMessageType messageType() {
		return ELunarMessageType.UPDATE_DATA;
	}
}