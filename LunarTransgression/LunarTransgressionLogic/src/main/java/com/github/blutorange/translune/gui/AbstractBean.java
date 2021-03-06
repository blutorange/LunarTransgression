package com.github.blutorange.translune.gui;

import java.io.IOException;
import java.util.function.Supplier;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;

import com.github.blutorange.common.ThrowingRunnable;
import com.github.blutorange.common.ThrowingSupplier;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.ComponentFactory;

@SuppressWarnings("serial")
public abstract class AbstractBean {
	@Inject @Classed(AbstractBean.class)
	Logger logger;

	public AbstractBean() {
		ComponentFactory.getLunarComponent().inject(this);
	}

	protected void redirect(final String relativeUrl) {
		try {
			final String url = getContextPath() + relativeUrl;
			logger.debug("redirecting to " + url);
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		}
		catch (final IOException e) {
			logger.error("could not redirect user to " + relativeUrl, e);
		}
	}

	public String getContextPath() {
		return FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath();
	}

	protected void addMessage(final Severity severity, final String summary, @Nullable final String details) {
		final FacesMessage message = new FacesMessage(severity, summary, details);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	protected void addMessage(final Severity severity, final String summary) {
		addMessage(severity, summary, null);
	}

	protected void safe(final ThrowingRunnable<Exception> runnable) {
		try {
			runnable.run();
		}
		catch (final Exception e) {
			logger.error("failure during admin action", e);
			addMessage(FacesMessage.SEVERITY_ERROR, "failure during admin action", e.getMessage());
		}
	}

	protected <T> T safe(final ThrowingSupplier<T, Exception> runnable, final Supplier<T> defaultValue) {
		try {
			return runnable.get();
		}
		catch (final Exception e) {
			logger.error("failure during admin action", e);
			addMessage(FacesMessage.SEVERITY_ERROR, "failure during admin action", e.getMessage());
			return defaultValue.get();
		}
	}
}
