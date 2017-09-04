package com.github.blutorange.translune.logic;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.jsoniter.annotation.JsonProperty;

public class Orderable {
	private String name = StringUtils.EMPTY;
	private EOrderDirection direction = EOrderDirection.ASC;

	/**
	 * @return the lhs
	 */
	@JsonProperty(required = true)
	public String getName() {
		return name;
	}
	/**
	 * @return the operator
	 */
	@JsonProperty(required = false)
	public EOrderDirection getOrderDirection() {
		return direction;
	}
	/**
	 * @param lhs the lhs to set
	 */
	public void setName(@Nullable final String name) {
		this.name = name != null ? name : StringUtils.EMPTY;
	}
	/**
	 * @param direction the operator to set
	 */
	public void setOperator(@Nullable final EOrderDirection direction) {
		this.direction= direction != null ? direction : EOrderDirection.ASC;
	}

	public Order toOrder(final Root<?> root, final CriteriaBuilder cb) {
		return getOrderDirection().apply(root.get(getName()), cb);
	}
}
