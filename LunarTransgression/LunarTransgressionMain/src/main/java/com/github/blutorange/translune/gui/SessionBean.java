package com.github.blutorange.translune.gui;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.Constants;
import com.github.blutorange.translune.CustomProperties;
import com.github.blutorange.translune.PasswordStorage;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.ILunarDatabaseManager.ELunarDatabaseManagerMock;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.InjectionUtil;

@ManagedBean(eager = true, name = "sessionBean")
@SessionScoped
public class SessionBean extends AbstractBean implements Serializable {
	private String username = StringUtils.EMPTY;

	private String password = StringUtils.EMPTY;

	private String passwordRepeat = StringUtils.EMPTY;

	private String nickname = StringUtils.EMPTY;

	@Transient
	@Inject
	ILunarDatabaseManager dbManager = ELunarDatabaseManagerMock.INSTANCE;

	@Transient
	@Inject
	CustomProperties customProperties;

	private EUserType userType = EUserType.ANONYMOUS;

	@PostConstruct
	void init() {
		InjectionUtil.inject(this);
	}

	/**
	 * @return the usernane
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param usernane
	 *            the usernane to set
	 */
	public void setUsername(@Nullable final String usernane) {
		this.username = usernane != null ? usernane : StringUtils.EMPTY;
	}

	/**
	 * @return the passwordRepeat
	 */
	public String getPasswordRepeat() {
		return passwordRepeat;
	}

	/**
	 * @param passwordRepeat
	 *            the passwordRepeat to set
	 */
	public void setPasswordRepeat(final String passwordRepeat) {
		this.passwordRepeat = passwordRepeat;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(@Nullable final String password) {
		this.password = password != null ? password : StringUtils.EMPTY;
	}

	public EUserType getUserType() {
		return userType;
	}

	/**
	 * @return the nickname of the current user
	 */
	public String getNickname() {
		return nickname;
	}

	public void logout() {
		this.userType = EUserType.ANONYMOUS;
		this.password = "";
		this.username = "";
		this.nickname = "";
		safe(() -> FacesContext.getCurrentInstance().getExternalContext()
				.redirect(contextPath() + "/public/index.xhtml"));
	}

	private String contextPath() {
		return FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath();
	}

	public void register(final ActionEvent actionEvent) {
		if (StringUtils.isAllBlank(username) || StringUtils.isAllBlank(password)) {
			addMessage(FacesMessage.SEVERITY_WARN, "username or password missing");
			return;
		}
		if (!password.equals(passwordRepeat)) {
			addMessage(FacesMessage.SEVERITY_WARN, "password repeat not matching password");
			return;
		}
		final String username = this.username.trim();
		final String password = this.password;
		if (Constants.CUSTOM_KEY_SADMIN_PASS.equals(username) || dbManager.find(Player.class, username) != null) {
			addMessage(FacesMessage.SEVERITY_WARN, "nickname exists already");
			return;
		}
		// TODO create new player with chars and stuff
		addMessage(FacesMessage.SEVERITY_INFO, "registration complete");
	}


	public void login(final ActionEvent actionEvent) {
		if (StringUtils.isAllBlank(username) || StringUtils.isEmpty(password)) {
			addMessage(FacesMessage.SEVERITY_WARN, "username or password missing");
			return;
		}
		final String username = this.username.trim();
		final String password = this.password;
		safe(() -> {
			final String redirect;
			if (Constants.USERNAME_SADMIN.equals(username)) {
				final String hash = customProperties.getSadminPass();
				if (hash == null || !PasswordStorage.verifyPassword(password, hash)) {
					addMessage(FacesMessage.SEVERITY_ERROR, "username or password wrong");
					return;
				}
				// Authorized
				userType = EUserType.SADMIN;
				redirect = "/public/admin.xhtml";
			}
			else {
				final Player player = dbManager.find(Player.class, username);
				if (player == null || !player.verifyPassword(password)) {
					addMessage(FacesMessage.SEVERITY_ERROR, "username or password wrong");
					return;
				}
				userType = EUserType.PLAYER;
				nickname = username;
				redirect = "/public/player.xhtml";
			}
			this.password = "";
			this.username = "";
			FacesContext.getCurrentInstance().getExternalContext().redirect(contextPath() + redirect);
		});
	}
}