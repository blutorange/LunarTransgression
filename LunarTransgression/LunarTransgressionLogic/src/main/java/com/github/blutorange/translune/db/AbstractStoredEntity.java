package com.github.blutorange.translune.db;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.function.Consumer;

import com.github.blutorange.common.IAccessible;
import com.jsoniter.annotation.JsonIgnore;

public abstract class AbstractStoredEntity extends AbstractEntity {
	@JsonIgnore
	public abstract EEntityMeta getEntityMeta();

	abstract void forEachAssociatedObject(Consumer<IAccessible<AbstractStoredEntity>> consumer);

	protected <T extends AbstractStoredEntity> void associated(final List<T> list, final Consumer<IAccessible<AbstractStoredEntity>> consumer) {
		final ListIterator<T> iterator = list.listIterator();
		final AccessibleListImpl<T> accessible = new AccessibleListImpl<>(iterator);
		while (iterator.hasNext()) {
			accessible.next();
			consumer.accept((IAccessible<AbstractStoredEntity>)accessible);
		}
	}

	protected <T extends AbstractStoredEntity> void associated(final Collection<T> set, final Consumer<IAccessible<AbstractStoredEntity>> consumer) {
		final Set<T> replacementSet = new HashSet<>();
		final AccessibleSetImpl<T> accessible = new AccessibleSetImpl<>(replacementSet);
		for (final T entity : set) {
			accessible.setEntity(entity);
			consumer.accept((IAccessible<AbstractStoredEntity>)accessible);
		}
		set.addAll(replacementSet);
	}
	protected class AccessibleSetImpl<S extends AbstractStoredEntity> implements IAccessible<S> {
		private final Set<S> set;
		private S entity = null;
		public AccessibleSetImpl(final Set<S> set) {
			this.set = set;
		}
		protected void setEntity(final S entity) {
			this.entity = entity;
		}
		@Override
		public S get() {
			return entity;
		}
		@Override
		public void set(final S replacement) {
			set.add(replacement);
		}
	}


	protected class AccessibleListImpl<S extends AbstractStoredEntity> implements IAccessible<S> {
		private final ListIterator<S> iterator;
		private S entity = null;
		AccessibleListImpl(final ListIterator<S> iterator) {
			this.iterator = iterator;
		}
		protected void next() {
			entity = iterator.next();
		}
		@Override
		public S get() {
			return entity;
		}
		@Override
		public void set(final S replacement) {
			iterator.set(replacement);
		}
	}
}