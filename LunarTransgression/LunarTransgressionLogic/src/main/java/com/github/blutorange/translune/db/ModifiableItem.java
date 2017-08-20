package com.github.blutorange.translune.db;

import com.github.blutorange.translune.logic.EItemEffect;

public class ModifiableItem extends ModifiableEntity<Item> {
	public void setEffect(final EItemEffect effect) {
		entity.setEffect(effect);
	}

	public void setName(final String name) {
		entity.setName(name);
	}

	public void setPower(final int power) {
		entity.setPower(power);
	}
}
