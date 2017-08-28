package com.github.blutorange.translune;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("nls")
public class LunarServletContainerInitializer implements ServletContainerInitializer {
	private final static Logger LOG = LoggerFactory.getLogger(ServletContainerInitializer.class);

	@Override
	public void onStartup(final @Nullable Set<@Nullable Class<?>> classes, final @Nullable ServletContext servletContext)
			throws ServletException {
		init();
	}

	void init() throws ServletException {
		try {
			LOG.info("initializing application server");
//			new JsoniterConfig().setup();
		}
		catch (final Exception e) {
			LOG.error("failed to process lunar initializer", e);
			throw new ServletException("failed to process lunar initializer", e);
		}
	}
}