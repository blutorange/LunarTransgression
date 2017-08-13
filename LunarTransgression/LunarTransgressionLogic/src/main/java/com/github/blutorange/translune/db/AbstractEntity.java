package com.github.blutorange.translune.db;

import java.io.Serializable;

import org.eclipse.jdt.annotation.Nullable;

public abstract class AbstractEntity {
	@Override
	public abstract boolean equals(@Nullable Object other);

	@Override
	public abstract int hashCode();

	@Override
	public abstract String toString();

	public abstract Serializable getPrimaryKey();

	public abstract EEntityMeta getEntityMeta();
}