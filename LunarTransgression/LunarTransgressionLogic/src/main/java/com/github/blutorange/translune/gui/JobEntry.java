package com.github.blutorange.translune.gui;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

public class JobEntry {
	String description;
	String name;
	String group;
	@Nullable
	private final Date next;
	@Nullable
	private final Date last;
	private final int priority;

	public JobEntry(final String name, @Nullable final String group, @Nullable final String description, final int priority,
			@Nullable final Date last, @Nullable final Date next) {
		this.name = name;
		this.description = description != null ? description : StringUtils.EMPTY;
		this.group = group != null ? group : StringUtils.EMPTY;
		this.priority = priority;
		this.last = last;
		this.next = next;
	}

	/**
	 * @return the next
	 */
	@Nullable
	public Date getNext() {
		return next;
	}

	/**
	 * @return the last
	 */
	@Nullable
	public Date getLast() {
		return last;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

}
