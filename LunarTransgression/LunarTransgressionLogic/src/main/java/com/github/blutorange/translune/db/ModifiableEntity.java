package com.github.blutorange.translune.db;

public abstract class ModifiableEntity<T> {
	protected T entity;
	public ModifiableEntity() {
	}
	void setEntity(final T entity) {
		this.entity = entity;
	}
}