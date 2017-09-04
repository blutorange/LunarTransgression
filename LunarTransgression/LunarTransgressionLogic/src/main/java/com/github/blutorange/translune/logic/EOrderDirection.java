package com.github.blutorange.translune.logic;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;

public enum EOrderDirection {
	ASC {
		@Override
		public Order apply(final Path<?> path, final CriteriaBuilder cb) {
			return cb.asc(path);
		}
	},
	DESC {
		@Override
		public Order apply(final Path<?> path, final CriteriaBuilder cb) {
			return cb.desc(path);
		}
	};

	public abstract Order apply(final Path<?> path, final CriteriaBuilder cb);
}