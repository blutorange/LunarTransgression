package com.github.blutorange.translune.gui;

import java.io.IOException;
import java.util.zip.ZipFile;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.serial.ImportProcessing;

@ManagedBean(eager = true, name = "manageBean")
@ViewScoped
public class ManageBean extends AbstractBean {
	@Inject @Classed(ManageBean.class)
	Logger logger;

	@Inject
	ImportProcessing importProcessing;

	public ManageBean() {
		ComponentFactory.getBeanComponent().inject(this);
	}

	public void handleImportUpload(final FileUploadEvent event) {
		FacesMessage message = null;
		try (ZipFile zipFile = new ZipFile(event.getFile().getFileName())) {
			importProcessing.importDataSet(zipFile);
			message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
		}
		catch (final IOException e) {
			logger.error("could not process uploaded file", e);
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not process the uploaded import: ", e.getMessage());
		}
		event.getFile();
		if (message != null)
			FacesContext.getCurrentInstance().addMessage(null, message);
	}


}