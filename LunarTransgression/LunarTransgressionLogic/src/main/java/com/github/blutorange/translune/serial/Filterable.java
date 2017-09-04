package com.github.blutorange.translune.serial;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.jsoniter.annotation.JsonProperty;

public class Filterable {
	private String lhs = StringUtils.EMPTY;
	private String operator = StringUtils.EMPTY;
	private String rhs = StringUtils.EMPTY;

	/**
	 * @return the lhs
	 */
	@JsonProperty(required = true)
	public String getLhs() {
		return lhs;
	}
	/**
	 * @return the operator
	 */
	@JsonProperty(required = true)
	public String getOperator() {
		return operator;
	}
	/**
	 * @return the rhs
	 */
	@JsonProperty(required = false)
	public String getRhs() {
		return rhs;
	}
	/**
	 * @param lhs the lhs to set
	 */
	public void setLhs(@Nullable final String lhs) {
		this.lhs = lhs != null ? lhs : StringUtils.EMPTY;
	}
	/**
	 * @param operator the operator to set
	 */
	public void setOperator(@Nullable final String operator) {
		this.operator = operator != null ? operator : StringUtils.EMPTY;
	}
	/**
	 * @param rhs the rhs to set
	 */
	public void setRhs(@Nullable final String rhs) {
		this.rhs = rhs != null ? rhs : StringUtils.EMPTY;
	}
}
