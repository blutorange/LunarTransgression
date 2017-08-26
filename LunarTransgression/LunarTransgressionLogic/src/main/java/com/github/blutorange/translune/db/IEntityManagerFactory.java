package com.github.blutorange.translune.db;

import java.io.IOException;

import javax.persistence.EntityManagerFactory;

import com.github.blutorange.common.ThrowingSupplier;

public interface IEntityManagerFactory extends ThrowingSupplier<EntityManagerFactory, IOException> {
	// Convenience interface binding the generics
}