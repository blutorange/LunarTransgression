package com.github.blutorange.translune.gui;

public enum EAdminPage {
	DATABASE("/public/admin_database.xhtml"),
	CHARACTER("/public/admin_manage.xhtml"),
	GAME_STATS("/public/admin_gamestats.xhtml"),
	SERVER_STATUS("/public/admin_serverstatus.xhtml"),
	;
	private String path;
	private EAdminPage(final String path) {
		this.path = path;
	}
	public String getPath() {
		return path;
	}
}
