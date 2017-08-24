package com.github.blutorange.translune.serial;

import java.util.Set;

import com.github.blutorange.translune.db.Skill;

public class CharacterToSkillCsvModel {
	private String characterId;
	private String skillId;
	private int level;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CharacterToSkillCsvModel other = (CharacterToSkillCsvModel) obj;
		if (characterId == null) {
			if (other.characterId != null)
				return false;
		} else if (!characterId.equals(other.characterId))
			return false;
		if (level != other.level)
			return false;
		if (skillId == null) {
			if (other.skillId != null)
				return false;
		} else if (!skillId.equals(other.skillId))
			return false;
		return true;
	}

	public String getCharacterId() {
		return characterId;
	}

	public int getLevel() {
		return level;
	}

	public String getSkillId() {
		return skillId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((characterId == null) ? 0 : characterId.hashCode());
		result = prime * result + level;
		result = prime * result + ((skillId == null) ? 0 : skillId.hashCode());
		return result;
	}

	public void setCharacterId(String characterId) {
		this.characterId = characterId;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}
}