package com.github.blutorange.translune.logic;

import com.github.blutorange.common.IntToIntFunction;
import com.github.blutorange.common.MathUtil;
import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.util.Constants;

class ComputedStatus implements IComputedStatus {
	private int accuracy;
	private int evasion;
	private int hp;
	private int magicalAttack;
	private int magicalDefense;
	private int mp;
	private int physicalAttack;
	private int physicalDefense;
	private int speed;
	private int cachedLevel = 0;
	
	protected final CharacterState characterState;

	public ComputedStatus(final CharacterState characterState) {
		this.characterState = characterState;
		_update();
	}

	@Override
	public int getComputedAccuracy() {
		_update();
		return accuracy;
	}

	@Override
	public int getComputedEvasion() {
		_update();
		return evasion;
	}

	@Override
	public int getComputedMaxHp() {
		_update();
		return hp;
	}

	@Override
	public int getComputedMagicalAttack() {
		_update();
		return magicalAttack;
	}

	@Override
	public int getComputedMagicalDefense() {
		_update();
		return magicalDefense;
	}

	@Override
	public int getComputedMaxMp() {
		_update();
		return mp;
	}

	@Override
	public int getComputedPhysicalAttack() {
		_update();
		return physicalAttack;
	}

	@Override
	public int getComputedPhysicalDefense() {
		_update();
		return physicalDefense;
	}

	@Override
	public int getComputedSpeed() {
		_update();
		return speed;
	}

	private void _update() {
		if (characterState.getLevel() == cachedLevel)
			return;
		hp = computedHp();
		mp = computedMp();
		physicalAttack = computedPhysicalAttack();
		physicalDefense = computedPhysicalDefense();
		magicalAttack = computedMagicalAttack();
		magicalDefense = computedMagicalDefense();
		speed = computedSpeed();
		accuracy = computedAccuracy();
		evasion = computedEvasion();
		cachedLevel = characterState.getLevel();
	}

	private int computed(final int base, final int level, final int iv, final int max, final IntToIntFunction nature) {
		final int releaseCount = characterState.getPlayer().getReleasedCharacterStatesCount();
		final int pre = ((2 * base + iv) * (level + releaseCount / 4)) / 100 + 5;
		final int post = nature.apply(pre);
		return MathUtil.clamp(post, 0, max);
	}

	private int computedAccuracy() {
		return characterState.getCharacter().getAccuracy();
	}

	private int computedEvasion() {
		return characterState.getCharacter().getEvasion();
	}

	private int computedHp() {
		final ENature nature = characterState.getNature();
		return computedHpMp(characterState.getCharacter().getMaxHp(), characterState.getLevel(),
				characterState.getIvHp(), Constants.MAX_HP, nature::adjustHp);
	}

	private int computedHpMp(final int base, final int level, final int iv, final int max,
			final IntToIntFunction nature) {
		final int releaseCount = characterState.getPlayer().getReleasedCharacterStatesCount();
		final int pre = ((((2 * base + iv) * (level + releaseCount / 4)) / 100) + level + 10);
		final int post = nature.apply(pre);
		return MathUtil.clamp(post, 0, max);
	}

	private int computedMagicalAttack() {
		final ENature nature = characterState.getNature();
		return computed(characterState.getCharacter().getMagicalAttack(), characterState.getLevel(),
				characterState.getIvMagicalAttack(), Constants.MAX_MAGICAL_ATTACK, nature::adjustMagicalAttack);
	}

	private int computedMagicalDefense() {
		final ENature nature = characterState.getNature();
		return computed(characterState.getCharacter().getMagicalDefense(), characterState.getLevel(),
				characterState.getIvMagicalDefense(), Constants.MAX_MAGICAL_DEFENSE, nature::adjustMagicalDefense);
	}

	private int computedMp() {
		final ENature nature = characterState.getNature();
		return computedHpMp(characterState.getCharacter().getMaxMp(), characterState.getLevel(),
				characterState.getIvMp(), Constants.MAX_MP, nature::adjustMp);
	}

	private int computedPhysicalAttack() {
		final ENature nature = characterState.getNature();
		return computed(characterState.getCharacter().getPhysicalAttack(), characterState.getLevel(),
				characterState.getIvPhysicalAttack(), Constants.MAX_PHYSICAL_ATTACK, nature::adjustPhysicalAttack);
	}

	private int computedPhysicalDefense() {
		final ENature nature = characterState.getNature();
		return computed(characterState.getCharacter().getPhysicalDefense(), characterState.getLevel(),
				characterState.getIvPhysicalDefense(), Constants.MAX_PHYSICAL_DEFENSE, nature::adjustPhysicalDefense);
	}

	private int computedSpeed() {
		final ENature nature = characterState.getNature();
		return computed(characterState.getCharacter().getSpeed(), characterState.getLevel(),
				characterState.getIvSpeed(), Constants.MAX_SPEED, nature::adjustSpeed);
	}

	@Override
	public CharacterState getCharacterState() {
		return characterState;
	}

	@Override
	public IComputedStatus getSnapshot() {
		return new ComputedStatusSnapshot();
	}
	
	protected class ComputedStatusSnapshot implements IComputedStatus {
		private int accuracy;
		private int evasion;
		private int maxHp;
		private int magicalAttack;
		private int magicalDefense;
		private int maxMp;
		private int physicalAttack;
		private int physicalDefense;
		private int speed;
		
		public ComputedStatusSnapshot() {
			accuracy = getComputedAccuracy();
			evasion = getComputedEvasion();
			maxHp = getComputedMaxHp();
			maxMp = getComputedMaxMp();
			magicalAttack = getComputedMagicalAttack();
			magicalDefense = getComputedMagicalDefense();
			maxMp = getComputedMaxMp();
			physicalAttack = getComputedPhysicalAttack();
			physicalDefense = getComputedPhysicalDefense();
			speed = getComputedSpeed();
		}

		@Override
		public int getComputedAccuracy() {
			return accuracy;
		}

		@Override
		public int getComputedEvasion() {
			return evasion;
		}

		@Override
		public int getComputedMaxHp() {
			return maxHp;
		}

		@Override
		public int getComputedMagicalAttack() {
			return magicalAttack;
		}

		@Override
		public int getComputedMagicalDefense() {
			return magicalDefense;
		}

		@Override
		public int getComputedMaxMp() {
			return maxMp;
		}

		@Override
		public int getComputedPhysicalAttack() {
			return physicalAttack;
		}

		@Override
		public int getComputedPhysicalDefense() {
			return physicalDefense;
		}

		@Override
		public int getComputedSpeed() {
			return speed;
		}

		@Override
		public CharacterState getCharacterState() {
			return characterState;
		}

		@Override
		public IComputedStatus getSnapshot() {
			return this;
		}
	}
}