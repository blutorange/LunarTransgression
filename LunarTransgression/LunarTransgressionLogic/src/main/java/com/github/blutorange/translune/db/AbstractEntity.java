package com.github.blutorange.translune.db;

import java.io.Serializable;

import org.eclipse.jdt.annotation.Nullable;

public abstract class AbstractEntity {
	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(@Nullable final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AbstractEntity other = (AbstractEntity) obj;
		if (!getPrimaryKey().equals(other.getPrimaryKey()))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getPrimaryKey().hashCode();
		return result;
	}

	@Override
	public abstract String toString();

	public abstract Serializable getPrimaryKey();
}