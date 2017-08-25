package com.github.blutorange.translune.gui;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;

import org.primefaces.component.inputswitch.InputSwitch;
import org.slf4j.Logger;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.serial.IImportProcessing;
import com.github.blutorange.translune.util.CustomProperties;

@ManagedBean(eager = true, name = "statusBean")
@ViewScoped
public class StatusBean extends AbstractBean {
	@SuppressWarnings("hiding")
	@Inject
	@Classed(StatusBean.class)
	Logger logger;

	@Inject
	IImportProcessing importProcessing;

	@Inject
	CustomProperties customProperties;

	@Inject
	ILunarDatabaseManager databaseManager;

	public StatusBean() {
		ComponentFactory.getBeanComponent().inject(this);
	}

	/**
	 * @return the online
	 */
	public boolean isOnline() {
		return customProperties.isOnline();
	}

	public void databaseCheck(final ActionEvent actionEvent) {
		try {
			databaseManager.checkConnection();
			final FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Database connection attempt successful", "");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		catch (final Exception e) {
			logger.error("Could not connect to the database", e);
			final FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to connect to the database",
					e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public void onOnlineChange(final AjaxBehaviorEvent event) {
		final InputSwitch inputSwitch = (InputSwitch)event.getComponent();
		setOnline(inputSwitch.isDisabled());
	}

	/**
	 * @param online
	 *            the online to set
	 */
	public void setOnline(final boolean online) {
		customProperties.setOnline(online);
	}
}