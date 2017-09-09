package com.github.blutorange.translune.db;

import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.translune.logic.EActionTarget;
import com.github.blutorange.translune.logic.EItemEffect;

public class ModifiableItem extends ModifiableEntity<Item> {
	public void setEffect(final EItemEffect effect) {
		entity.setEffect(effect);
	}

	public void setImgIcon(final String imgIcon) {
		entity.setImgIcon(imgIcon);
	}

	public void setName(@NonNull final String name) {
		entity.setName(name);
	}

	public void setPower(final int power) {
		entity.setPower(power);
	}

	public void setPriority(final int priority) {
		entity.setPriority(priority);
	}

	public void setTarget(final EActionTarget target) {
		entity.setTarget(target);
	}
}
