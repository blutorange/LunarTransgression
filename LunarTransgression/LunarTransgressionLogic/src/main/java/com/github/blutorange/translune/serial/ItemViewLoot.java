package com.github.blutorange.translune.serial;

import org.apache.commons.lang3.StringUtils;

import com.github.blutorange.translune.db.Item;
import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;

public class ItemViewLoot {
	private final String name;
	private final String imgIcon;

	@Deprecated
	public ItemViewLoot() {
		name = StringUtils.EMPTY;
		imgIcon = StringUtils.EMPTY;
	}

	public ItemViewLoot(final Item item) {
		this.name = item.getName();
		this.imgIcon = item.getImgIcon();
	}

	/**
	 * @return the level
	 */
	@JsonProperty(required = true)
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public String getName() {
		return name;
	}
	/**
	 * @return the imgIcon
	 */
	@JsonProperty(required = true)
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public String getImgIcon() {
		return imgIcon;
	}
}