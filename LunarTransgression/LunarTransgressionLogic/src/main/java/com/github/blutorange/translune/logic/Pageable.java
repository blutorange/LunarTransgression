package com.github.blutorange.translune.logic;

import java.util.Arrays;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.serial.Filterable;
import com.jsoniter.annotation.JsonProperty;

public class Pageable {
	private int offset = 0;
	private int count = 10;
	private Orderable[] orderBy = new Orderable[0];
	private Filterable[] filter = new Filterable[0];
	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
	/**
	 * @return the sort
	 */
	@JsonProperty(collectionValueNullable = false)
	public Orderable[] getOrderBy() {
		return orderBy;
	}
	/**
	 * @return the filter
	 */
	@JsonProperty(collectionValueNullable = false)
	public Filterable[] getFilter() {
		return filter;
	}
	/**
	 * @param offset the offset to set
	 */
	public void setOffset(final int offset) {
		this.offset = offset < 0 ? 0 : offset;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(final int count) {
		this.count = count < 0 ? 0 : count;
	}
	/**
	 * @param orderBy the sort to set
	 */
	public void setOrderBy(final Orderable @Nullable[] orderBy) {
		this.orderBy = orderBy != null ? orderBy : new Orderable[0];
	}
	/**
	 * @param filter the filter to set
	 */
	public void setFilter(final Filterable @Nullable[] filter) {
		this.filter = filter != null ? filter : new Filterable[0];
	}

	public Order[] toOrders(final Root<?> root, final CriteriaBuilder cb) {
		return Arrays.stream(getOrderBy()).map(order -> order.toOrder(root, cb)).toArray(Order[]::new);
	}
}