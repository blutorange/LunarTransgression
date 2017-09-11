package com.github.blutorange.translune.serial;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.Skill;
import com.jsoniter.annotation.JsonIgnore;

public class CharacterViewBattle {
	private final int level;
	private final String imgFront;
	private final String imgBack;
	private final String nickname;
	private final String cry;
	private final int exp;
	private final int expNext;
	private final Map<Skill, Integer> skills;

	@Deprecated
	public CharacterViewBattle() {
		level = 1;
		imgFront = StringUtils.EMPTY;
		imgBack = StringUtils.EMPTY;
		nickname = StringUtils.EMPTY;
		cry = StringUtils.EMPTY;
		exp = 0;
		expNext = 0;
		skills = JsoniterCollections.emptyMap();
	}

	public CharacterViewBattle(final CharacterState characterState) {
		level = characterState.getLevel();
		exp = characterState.getExp();
		cry = characterState.getCharacter().getCry();
		expNext = characterState.getCharacter().getExperienceGroup().getExperienceForLevel(level+1);
		skills = characterState.getCharacter().getUnmodifiableSkills();
		nickname = characterState.getNickname();
		imgFront = characterState.getCharacter().getImgFront();
		imgBack = characterState.getCharacter().getImgBack();
	}

	/**
	 * @return the level
	 */
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public int getLevel() {
		return level;
	}
	/**
	 * @return the imgIcon
	 */
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public String getImgFront() {
		return imgFront;
	}

	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public String getImgBack() {
		return imgBack;
	}

	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public String getCry() {
		return cry;
	}

	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public String getNickname() {
		return nickname;
	}

	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public int getExp() {
		return exp;
	}

	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public int getExpNext() {
		return expNext;
	}

	public Map<Skill, Integer> getSkills() {
		return skills;
	}
}