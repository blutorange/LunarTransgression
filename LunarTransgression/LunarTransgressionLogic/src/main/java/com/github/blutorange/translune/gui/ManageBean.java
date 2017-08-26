package com.github.blutorange.translune.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipFile;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.persistence.Transient;

import org.apache.commons.io.IOUtils;
import org.primefaces.component.inputswitch.InputSwitch;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.serial.IImportProcessing;

@ManagedBean(eager = true, name = "manageBean")
@ViewScoped
public class ManageBean extends AbstractBean {
	@SuppressWarnings("hiding")
	@Inject
	@Classed(ManageBean.class)
	Logger logger;

	@Transient
	@Inject
	IImportProcessing importProcessing;

	@Transient
	@Inject
	ILunarDatabaseManager databaseManager;

	private boolean emptyDatabase;

	public ManageBean() {
	}

	@PostConstruct
	void postConstruct() {
		ComponentFactory.getLunarComponent().inject(this);
	}

	public void handleImportUpload(final FileUploadEvent event) {
		FacesMessage message = null;
		final String filename = event.getFile().getFileName();
		logger.debug("attempting to process import file " + filename);
		File file = null;
		try {
			logger.debug("requested empty database: " + emptyDatabase);
			if (emptyDatabase)
				databaseManager.createSchema();
			file = File.createTempFile("lunar", ".import");
			try (OutputStream os = new FileOutputStream(file)) {
				IOUtils.copy(event.getFile().getInputstream(), os);
			}
			try (ZipFile zipFile = new ZipFile(file, StandardCharsets.UTF_8)) {
				final int count = importProcessing.importDataSet(zipFile);
				message = new FacesMessage("Import processed successfully",
						String.format("%d entities imported.", Integer.valueOf(count)));
			}
		}
		catch (final Exception e) {
			logger.error("could not process uploaded file", e);
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not process the uploaded import",
					e.getMessage());
		}
		finally {
			try {
				if (file != null)
					file.delete();
			}
			catch (final Exception e) {
				logger.error("failed to delete temporary file ", e);
			}
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		RequestContext.getCurrentInstance().execute("PF('statusDialog').hide()");
	}

	/**
	 * @return the emptyDatabase
	 */
	public boolean isEmptyDatabase() {
		return emptyDatabase;
	}

	public void onEmptyDatabaseChange(final AjaxBehaviorEvent event) {
		final InputSwitch inputSwitch = (InputSwitch)event.getComponent();
		setEmptyDatabase(!inputSwitch.isDisabled());
	}

	/**
	 * @param emptyDatabase the emptyDatabase to set
	 */
	public void setEmptyDatabase(final boolean emptyDatabase) {
		this.emptyDatabase = emptyDatabase;
	}
}