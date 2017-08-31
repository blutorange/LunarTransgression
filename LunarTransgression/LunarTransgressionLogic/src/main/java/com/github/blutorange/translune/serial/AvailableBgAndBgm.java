package com.github.blutorange.translune.serial;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import com.jsoniter.annotation.JsonProperty;

public class AvailableBgAndBgm {
	private Map<String, Set<String>> bgmBattle;
	private Map<String, Set<String>> bgBattle;
	private Map<String, Set<String>> bgmMenu;
	private Map<String, Set<String>> bgMenu;

	@Deprecated
	public AvailableBgAndBgm() {
		bgBattle = Collections.emptyMap();
		bgmBattle = Collections.emptyMap();
		bgMenu = Collections.emptyMap();
		bgmMenu = Collections.emptyMap();
	}

	public AvailableBgAndBgm(final Map<String, Set<String>> bgMenu, final Map<String, Set<String>> bgmMenu,
			final Map<String, Set<String>> bgBattle, final Map<String, Set<String>> bgmBattle) {
		this.bgMenu = bgMenu;
		this.bgmMenu = bgmMenu;
		this.bgBattle = bgBattle;
		this.bgmBattle = bgmBattle;
	}

	@JsonProperty(required = true, collectionValueNullable = false)
	public Map<String, Set<String>> getBgMenu() {
		return bgMenu;
	}

	@JsonProperty(required = true, collectionValueNullable = false)
	public Map<String, Set<String>> getBgmMenu() {
		return bgmMenu;
	}

	@JsonProperty(required = true, collectionValueNullable = false)
	public Map<String, Set<String>> getBgBattle() {
		return bgBattle;
	}

	@JsonProperty(required = true, collectionValueNullable = false)
	public Map<String, Set<String>> getBgmBattle() {
		return bgmBattle;
	}

	/**
	 * @param bgmBattle
	 *            the bgmBattle to set
	 */
	public void setBgmBattle(@Nullable final Map<String, Set<String>> bgmBattle) {
		this.bgmBattle = bgmBattle != null ? bgmBattle : Collections.emptyMap();
	}

	/**
	 * @param bgBattle
	 *            the bgBattle to set
	 */
	public void setBgBattle(@Nullable final Map<String, Set<String>> bgBattle) {
		this.bgBattle = bgBattle != null ? bgBattle : Collections.emptyMap();
	}

	/**
	 * @param bgmMenu
	 *            the bgmMenu to set
	 */
	public void setBgmMenu(@Nullable final Map<String, Set<String>> bgmMenu) {
		this.bgmMenu = bgmMenu != null ? bgmMenu : Collections.emptyMap();
	}

	/**
	 * @param bgMenu
	 *            the bgMenu to set
	 */
	public void setBgMenu(@Nullable final Map<String, Set<String>> bgMenu) {
		this.bgMenu = bgMenu != null ? bgMenu : Collections.emptyMap();
	}
}