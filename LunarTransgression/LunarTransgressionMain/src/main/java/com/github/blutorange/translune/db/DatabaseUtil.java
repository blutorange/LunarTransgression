package com.github.blutorange.translune.db;

import java.util.Properties;

import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

public final class DatabaseUtil {
	private DatabaseUtil() {}

	public static void createSchema() throws PersistenceException {
		final Properties p = new Properties();
		p.put("javax.persistence.schema-generation.database.action", "drop-and-create");
		Persistence.generateSchema("hibernate", p);

//		final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().configure().build();
//		final Metadata metadata = new MetadataSources(serviceRegistry).buildMetadata();
//		final SchemaExport schemaExport = new SchemaExport();
//		schemaExport.execute(EnumSet.of(TargetType.DATABASE), Action.BOTH, metadata);

//		final Configuration c = new Configuration();
//		c.setProperty(Environment.HBM2DDL_AUTO, "create");
//		c.configure();
//		try (SessionFactory sf = c.buildSessionFactory()) {
//			// auto
//		}
	}
}
