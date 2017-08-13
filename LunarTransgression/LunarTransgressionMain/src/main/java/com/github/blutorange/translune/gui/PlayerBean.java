package com.github.blutorange.translune.gui;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.eclipse.jdt.annotation.Nullable;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.InjectionUtil;
import com.github.blutorange.translune.logic.InitIdStore;

@ManagedBean(eager=true, name="playerBean")
@ViewScoped
public class PlayerBean extends AbstractBean {

	@Inject @Classed(PlayerBean.class) Logger logger;

	@Nullable
	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean = null;

	@Inject
	InitIdStore initIdStore;

	@PostConstruct
	public void init() {
		InjectionUtil.inject(this);
	}

	public void startGame() {
		safe(() -> {
			if (saveGetSessionBean().getUserType() != EUserType.PLAYER) {
				addMessage(FacesMessage.SEVERITY_FATAL, "cannot start game, you are not a player");
				return;
			}
			final String nickname = saveGetSessionBean().getNickname();
			final String initId = initIdStore.store(nickname);
			RequestContext.getCurrentInstance().execute(String.format("startGame('%s')", initId));
			addMessage(FacesMessage.SEVERITY_INFO, "game starting...");
		});
	}

	public void setSessionBean(@Nullable final SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	@Nullable
	public SessionBean getSessionBean() throws IllegalStateException {
		return sessionBean;
	}

	public SessionBean saveGetSessionBean() throws IllegalStateException {
		final SessionBean sessionBean = this.sessionBean;
		if (sessionBean == null)
			throw new IllegalStateException("session bean is null");
		return sessionBean;
	}

}