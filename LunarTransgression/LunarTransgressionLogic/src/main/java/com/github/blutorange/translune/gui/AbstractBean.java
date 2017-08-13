package com.github.blutorange.translune.gui;

import java.util.function.Supplier;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;

import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.InjectionUtil;
import com.github.blutorange.translune.util.ThrowingRunnable;
import com.github.blutorange.translune.util.ThrowingSupplier;

public abstract class AbstractBean {
	@Inject @Classed(AbstractBean.class)
	Logger logger;

	public AbstractBean() {
		InjectionUtil.inject(this);
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
