package com.github.blutorange.translune.gui;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.Character;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.ComponentFactory;

@ManagedBean(eager = true, name = "adminBean")
@ViewScoped
public class AdminBean extends AbstractBean {
	@Nullable
	private EAdminPage adminPage;

	@Nullable
	private Gamestats gamestats;

	@Inject
	ILunarDatabaseManager lunarDatabaseManager;

	@PostConstruct
	public void init() {
		adminPage = EAdminPage.SERVER_STATUS;
		ComponentFactory.getBeanComponent().inject(this);
	}

	@Nullable
	public EAdminPage getPage() {
		return adminPage;
	}

	public void setPage(@Nullable final EAdminPage page) {
		this.adminPage = page;
	}

	/**
	 * @param actionEvent
	 *            The event that triggered this method.
	 */
	public void createSchema(final ActionEvent actionEvent) {
		safe(() -> {
			lunarDatabaseManager.createSchema();
			addMessage(FacesMessage.SEVERITY_INFO, "Database schema created");
		});
	}

	@Nullable
	public Gamestats getGamestats() {
		return safe(() -> {
			Gamestats gamestats = this.gamestats;
			if (gamestats != null)
				return gamestats;
			return gamestats = createGamestats();
		}, () -> new Gamestats());
	}

	public void refreshGamestats() {
		this.gamestats = createGamestats();
	}

	private Gamestats createGamestats() {
		final ILunarDatabaseManager ldm = lunarDatabaseManager;
		return new Gamestats(ldm.count(Player.class), ldm.count(Character.class), ldm.count(Item.class));
	}
}