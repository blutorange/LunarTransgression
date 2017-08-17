package com.github.blutorange.translune.ic;

public class ComponentFactory {
	public static StorageComponent createStorageComponent() {
		return DaggerStorageComponent.create();
	}

	public static BeanComponent createBeanComponent() {
		return DaggerBeanComponent.create();
	}

	public static SocketComponent createSocketComponent() {
		return DaggerSocketComponent.create();
	}

	public static LogicComponent createLogicComponent() {
		return DaggerLogicComponent.create();
	}
}