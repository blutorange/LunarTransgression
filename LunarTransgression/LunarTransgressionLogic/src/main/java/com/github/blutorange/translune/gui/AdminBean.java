package com.github.blutorange.translune.gui;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.persistence.Transient;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.ComponentFactory;

@ManagedBean(eager = true, name = "adminBean")
@ViewScoped
public class AdminBean extends AbstractBean {
	@Nullable
	private EAdminPage adminPage;

	@Nullable
	private Gamestats gamestats;

	@Transient
	@Inject
	ILunarDatabaseManager lunarDatabaseManager;

	@PostConstruct
	public void init() {
		adminPage = EAdminPage.SERVER_STATUS;
		ComponentFactory.getLunarComponent().inject(this);
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
		try {
			this.gamestats = createGamestats();
		}
		catch (final IOException e) {
			logger.error("failed to update stats", e);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not refresh stats", e.getMessage()));
		}
	}

	private Gamestats createGamestats() throws IOException {
		final ILunarDatabaseManager ldm = lunarDatabaseManager;
		return new Gamestats(ldm.count(Player.class), ldm.count(CharacterState.class));
	}
}