package com.github.blutorange.translune.gui;

import java.net.URLEncoder;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.Transient;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jdt.annotation.Nullable;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.logic.IInitIdStore;

@ManagedBean(eager = true, name = "playerBean")
@ViewScoped
public class PlayerBean extends AbstractBean {

	@SuppressWarnings("hiding")
	@Transient
	@Inject
	@Classed(PlayerBean.class)
	Logger logger;

	@Nullable
	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean = null;

	@Transient
	@Inject
	IInitIdStore initIdStore;

	@PostConstruct
	public void init() {
		ComponentFactory.getLunarComponent().inject(this);
	}

	public void startGame() {
		safe(() -> {
			if (saveGetSessionBean().getUserType() != EUserType.PLAYER) {
				addMessage(FacesMessage.SEVERITY_FATAL, "cannot start game, you are not a player");
				return;
			}
			final String contextPath = FacesContext.getCurrentInstance().getExternalContext()
					.getApplicationContextPath();
			final String nickname = saveGetSessionBean().getNickname();
			final String initId = initIdStore.store(nickname);
			final String wsEndpoint = contextPath + "/ws/translune";
			final String url = String.format("%s/resources/translune/game.html?initId=%s&nickname=%s&wsEndpoint=%s",
					contextPath, URLEncoder.encode(initId, "UTF-8"), URLEncoder.encode(nickname, "UTF-8"),
					URLEncoder.encode(wsEndpoint, "UTF-8"));
			RequestContext.getCurrentInstance()
					.execute(String.format("Translune.startGame('%s')", StringEscapeUtils.escapeEcmaScript(url)));
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