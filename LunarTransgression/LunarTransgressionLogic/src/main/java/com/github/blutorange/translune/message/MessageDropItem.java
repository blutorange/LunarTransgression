package com.github.blutorange.translune.message;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarPayload;
import com.jsoniter.annotation.JsonProperty;

public class MessageDropItem implements ILunarPayload {
	String item = StringUtils.EMPTY;

	/**
	 * @return the details
	 */
	@JsonProperty(required = false)
	public String getItem() {
		return item;
	}

	/**
	 * @param details
	 *            the details to set
	 */
	public void setItem(@Nullable final String item) {
		this.item = item != null ? item : StringUtils.EMPTY;
	}

	@Override
	public @NonNull ELunarMessageType messageType() {
		return ELunarMessageType.DROP_ITEM;
	}
}