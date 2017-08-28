package com.github.blutorange.translune.serial;

import org.eclipse.jdt.annotation.Nullable;

public class CharacterToSkillCsvModel {
	@Nullable
	private String characterId;
	@Nullable
	private String skillId;
	private int level;

	@Override
	public boolean equals(@Nullable final Object obj) {
		final String characterId = this.characterId;
		final String skillId = this.skillId;
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CharacterToSkillCsvModel other = (CharacterToSkillCsvModel) obj;
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

	@Nullable
	public String getCharacterId() {
		return characterId;
	}

	public int getLevel() {
		return level;
	}


	@Nullable
	public String getSkillId() {
		return skillId;
	}

	@Override
	public int hashCode() {
		final String characterId = this.characterId;
		final String skillId = this.skillId;
		final int prime = 31;
		int result = 1;
		result = prime * result + ((characterId == null) ? 0 : characterId.hashCode());
		result = prime * result + level;
		result = prime * result + ((skillId == null) ? 0 : skillId.hashCode());
		return result;
	}

	public void setCharacterId(final String characterId) {
		this.characterId = characterId;
	}

	public void setLevel(final int level) {
		this.level = level;
	}

	public void setSkillId(final String skillId) {
		this.skillId = skillId;
	}

	@Override
	public String toString() {
		return String.format("CharacterToSkillCsvModel(%s,%s,%s)", level, characterId, skillId);
	}
}