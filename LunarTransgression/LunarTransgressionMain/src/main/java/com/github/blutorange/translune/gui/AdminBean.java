package com.github.blutorange.translune.gui;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.Character;
import com.github.blutorange.translune.db.DatabaseUtil;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.InjectionUtil;

@ManagedBean(eager=true, name="adminBean")
@ViewScoped
public class AdminBean extends AbstractBean {
	@Nullable
	private EAdminPage adminPage;

	@Nullable
	private Gamestats gamestats;

	@Inject
	EntityManagerFactory entityManagerFactory;

	@PostConstruct
	public void init() {
		adminPage = EAdminPage.SERVER_STATUS;
		InjectionUtil.inject(this);
	}

	@Nullable
	public EAdminPage getPage() {
		return adminPage;
	}

	public void setPage(@Nullable final EAdminPage page) {
		this.adminPage = page;
	}

	/**
	 * @param actionEvent The event that triggered this method.
	 */
	public void createSchema(final ActionEvent actionEvent) {
		safe(() -> {
			DatabaseUtil.createSchema();
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
		//TODO replace with db manager
		final EntityManager em = entityManagerFactory.createEntityManager();
		final Long countPlayer;
		final Long countCharacter;
		final Long countItem;
		try {
			final CriteriaBuilder cb = em.getCriteriaBuilder();
			final CriteriaQuery<Long> cqP = cb.createQuery(Long.class);
			final CriteriaQuery<Long> cqC = cb.createQuery(Long.class);
			final CriteriaQuery<Long> cqI = cb.createQuery(Long.class);
			countPlayer = em.createQuery(cqP.select(cb.count(cqP.from(Player.class)))).getSingleResult();
			countCharacter = em.createQuery(cqC.select(cb.count(cqC.from(Character.class)))).getSingleResult();
			countItem = em.createQuery(cqI.select(cb.count(cqI.from(Item.class)))).getSingleResult();
		}
		finally {
			em.close();
		}
		return new Gamestats(countPlayer, countCharacter, countItem);
	}
}