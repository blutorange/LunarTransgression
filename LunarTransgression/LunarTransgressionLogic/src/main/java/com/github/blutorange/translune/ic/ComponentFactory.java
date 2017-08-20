package com.github.blutorange.translune.ic;

public class ComponentFactory {
	private static final StorageComponent storageComponent = DaggerStorageComponent.create();
	private static final BeanComponent beanComponent = DaggerBeanComponent.create();
	private static final DatabaseComponent databaseComponent = DaggerDatabaseComponent.create();
	private static final SocketComponent socketComponent = DaggerSocketComponent.create();
	private static final LogicComponent logicComponent = DaggerLogicComponent.create();

	public static StorageComponent getStorageComponent() {
		return storageComponent;
	}

	public static BeanComponent getBeanComponent() {
		return beanComponent;
	}

	public static SocketComponent getSocketComponent() {
		return socketComponent;
	}

	public static LogicComponent getLogicComponent() {
		return logicComponent;
	}

	public static DatabaseComponent getDatabaseComponent() {
		return databaseComponent;
	}
}