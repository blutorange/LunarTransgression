package com.github.blutorange.translune.gui;

import java.io.IOException;

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

import com.github.blutorange.common.CannotPerformOperationException;
import com.github.blutorange.common.PasswordStorage;
import com.github.blutorange.translune.db.Character;
import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.CharacterStateBuilder;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.ILunarDatabaseManager.ELunarDatabaseManagerMock;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.db.PlayerBuilder;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.util.Constants;
import com.github.blutorange.translune.util.CustomProperties;

@SuppressWarnings("serial")
@ManagedBean(eager = true, name = "sessionBean")
@SessionScoped
public class SessionBean extends AbstractBean {
	private String username = StringUtils.EMPTY;

	private String password = StringUtils.EMPTY;

	private String passwordRepeat = StringUtils.EMPTY;

	private String nickname = StringUtils.EMPTY;

	@Transient
	@Inject
	public ILunarDatabaseManager databaseManager = ELunarDatabaseManagerMock.INSTANCE;

	@Transient
	@Inject
	CustomProperties customProperties;

	private EUserType userType = EUserType.ANONYMOUS;

	@PostConstruct
	void init() {
		ComponentFactory.getLunarComponent().inject(this);
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

	/**
	 * @param actionEvent
	 */
	public void register(final ActionEvent actionEvent) {
		if (StringUtils.isAllBlank(this.username) || StringUtils.isAllBlank(password)) {
			addMessage(FacesMessage.SEVERITY_WARN, "Username or password missing");
			return;
		}
		if (!password.equals(passwordRepeat)) {
			addMessage(FacesMessage.SEVERITY_WARN, "Password repeat not matching password");
			return;
		}
		final String nickname = this.username.trim();
		final String password = this.password;
		try {
			if (Constants.USERNAME_SADMIN.equals(nickname) || databaseManager.find(Player.class, nickname) != null) {
				addMessage(FacesMessage.SEVERITY_WARN, "Nickname exists already");
				return;
			}
			createPlayer(nickname, password);
		}
		catch (final Exception e) {
			logger.error("failed to create player", e);
			addMessage(FacesMessage.SEVERITY_ERROR, "Failed to create player: " + e.getMessage());
			return;
		}
		addMessage(FacesMessage.SEVERITY_INFO, "Registration complete");
		redirect("");
	}

	private void createPlayer(final String nickname, final String password) throws CannotPerformOperationException {
		logger.debug("creating player");
		Character[] characters;
		try {
			characters = databaseManager.findRandom(Character.class, 4);
		}
		catch (final IOException e) {
			logger.error("failed to create player", e);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not create a player", e.getMessage()));
			return;
		}
		logger.debug("found random characters: " + characters.length);
		if (characters.length != 4) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Failed to generate characters");
			return;
		}
		final CharacterStateBuilder characterStateBuilder = new CharacterStateBuilder();
		final CharacterState cs1 = characterStateBuilder.randomIvs().randomNature().setCharacter(characters[0]).build();
		final CharacterState cs2 = characterStateBuilder.randomIvs().randomNature().setCharacter(characters[1]).build();
		final CharacterState cs3 = characterStateBuilder.randomIvs().randomNature().setCharacter(characters[2]).build();
		final CharacterState cs4 = characterStateBuilder.randomIvs().randomNature().setCharacter(characters[3]).build();
		final Player player = new PlayerBuilder(nickname).setDescription("I joined the transmigration.").setPassword(password).addCharacterStates(cs1,cs2,cs3,cs4).build();

		databaseManager.persist(player, cs1, cs2, cs3, cs4);
	}

	/**
	 * @param actionEvent
	 */
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
				if (!PasswordStorage.verifyPassword(password, hash)) {
					addMessage(FacesMessage.SEVERITY_ERROR, "username or password wrong");
					return;
				}
				// Authorized
				userType = EUserType.SADMIN;
				redirect = "/public/admin.xhtml";
			}
			else {
				if (!customProperties.isOnline()) {
					addMessage(FacesMessage.SEVERITY_WARN, "server is currently in maintenance mode");
					return;
				}
				final Player player = databaseManager.find(Player.class, username);
				if (player == null || !player.verifyPassword(password)) {
					addMessage(FacesMessage.SEVERITY_ERROR, "username or password wrong, or cannot access database");
					return;
				}
				userType = EUserType.PLAYER;
				nickname = username;
				redirect = "/public/player.xhtml";
			}
			this.password = "";
			this.username = "";
			redirect(redirect);
		});
	}
}