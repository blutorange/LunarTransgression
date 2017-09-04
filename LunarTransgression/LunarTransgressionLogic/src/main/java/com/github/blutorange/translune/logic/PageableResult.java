package com.github.blutorange.translune.logic;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.jsoniter.annotation.JsonProperty;

public class PageableResult {
	int total = 0;
	int filteredTotal = 0;
	Object[] list = ArrayUtils.EMPTY_OBJECT_ARRAY;

	@Deprecated
	public PageableResult() {
	}

	public PageableResult(final int total, final int filteredTotal, final Object[] list) {
		this.total = total;
		this.filteredTotal = filteredTotal;
		this.list = list;
	}

	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * @return the filteredTotal
	 */
	public int getFilteredTotal() {
		return filteredTotal;
	}

	/**
	 * @return the list
	 */
	@JsonProperty(required = true)
	public Object[] getList() {
		return list;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(final int total) {
		this.total = total;
	}

	/**
	 * @param filteredTotal the filteredTotal to set
	 */
	public void setFilteredTotal(final int filteredTotal) {
		this.filteredTotal = filteredTotal;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(final Object @Nullable[] list) {
		this.list = list != null ? list : ArrayUtils.EMPTY_OBJECT_ARRAY;
	}
}