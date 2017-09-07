package com.github.blutorange.translune.message;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.handler.EHandlerFetchData;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;
import com.jsoniter.annotation.JsonProperty;

public class MessageFetchData implements ILunarPayload {
	String details = StringUtils.EMPTY;

	EHandlerFetchData fetch = EHandlerFetchData.NONE;

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
	public EHandlerFetchData getFetch() {
		return fetch;
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
	public void setFetch(@Nullable final EHandlerFetchData fetch) {
		this.fetch = fetch != null ? fetch : EHandlerFetchData.NONE;
	}

	@Override
	public @NonNull ELunarMessageType messageType() {
		return ELunarMessageType.FETCH_DATA;
	}
}