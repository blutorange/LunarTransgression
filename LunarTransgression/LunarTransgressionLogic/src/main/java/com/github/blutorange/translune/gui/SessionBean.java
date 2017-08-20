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

import com.github.blutorange.translune.CustomProperties;
import com.github.blutorange.translune.db.Character;
import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.CharacterStateBuilder;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.ILunarDatabaseManager.ELunarDatabaseManagerMock;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.db.PlayerBuilder;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.util.Constants;
import com.github.blutorange.translune.util.PasswordStorage;
import com.github.blutorange.translune.util.PasswordStorage.CannotPerformOperationException;

@SuppressWarnings("serial")
@ManagedBean(eager = true, name = "sessionBean")
@SessionScoped
public class SessionBean extends AbstractBean implements Serializable {
	private String username = StringUtils.EMPTY;

	private String password = StringUtils.EMPTY;

	private String passwordRepeat = StringUtils.EMPTY;

	private String nickname = StringUtils.EMPTY;

	@Transient
	@Inject
	ILunarDatabaseManager databaseManager = ELunarDatabaseManagerMock.INSTANCE;

	@Transient
	@Inject
	CustomProperties customProperties;

	private EUserType userType = EUserType.ANONYMOUS;

	@PostConstruct
	void init() {
		ComponentFactory.getBeanComponent().inject(this);
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
				.redirect(getContextPath() + "/public/index.xhtml"));
	}

	public String getContextPath() {
		return FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath();
	}

	public void register(final ActionEvent actionEvent) {
		if (StringUtils.isAllBlank(this.username) || StringUtils.isAllBlank(password)) {
			addMessage(FacesMessage.SEVERITY_WARN, "username or password missing");
			return;
		}
		if (!password.equals(passwordRepeat)) {
			addMessage(FacesMessage.SEVERITY_WARN, "password repeat not matching password");
			return;
		}
		final String nickname = this.username.trim();
		final String password = this.password;
		if (Constants.CUSTOM_KEY_SADMIN_PASS.equals(nickname) || databaseManager.find(Player.class, nickname) != null) {
			addMessage(FacesMessage.SEVERITY_WARN, "nickname exists already");
			return;
		}
		try {
			createPlayer(nickname, password);
		}
		catch (final CannotPerformOperationException e) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Failed to create player: " + e.getMessage());
			return;
		}
		addMessage(FacesMessage.SEVERITY_INFO, "registration complete");
	}

	private void createPlayer(final String nickname, final String password) throws CannotPerformOperationException {
		final Character[] characters = databaseManager.findRandom(Character.class, 4);
		if (characters.length != 4) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Failed to generate characters");
			return;
		}
		final CharacterStateBuilder characterStateBuilder = new CharacterStateBuilder();
		final CharacterState cs1 = characterStateBuilder.randomIvs().randomNature().setCharacter(characters[0]).build();
		final CharacterState cs2 = characterStateBuilder.randomIvs().randomNature().setCharacter(characters[1]).build();
		final CharacterState cs3 = characterStateBuilder.randomIvs().randomNature().setCharacter(characters[2]).build();
		final CharacterState cs4 = characterStateBuilder.randomIvs().randomNature().setCharacter(characters[3]).build();
		final Player player = new PlayerBuilder(nickname).setPassword(password).addCharacterStates(cs1,cs2,cs3,cs4).build();

		databaseManager.persist(player);
		databaseManager.persist(cs1);
		databaseManager.persist(cs2);
		databaseManager.persist(cs3);
		databaseManager.persist(cs4);
		databaseManager.persist(characters[0]);
		databaseManager.persist(characters[1]);
		databaseManager.persist(characters[2]);
		databaseManager.persist(characters[3]);
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
				final Player player = databaseManager.find(Player.class, username);
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
			FacesContext.getCurrentInstance().getExternalContext().redirect(getContextPath() + redirect);
		});
	}
}