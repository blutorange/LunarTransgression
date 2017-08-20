package com.github.blutorange.translune;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.ic.ComponentFactory;

@WebListener(value="Listener for the lunar application")
public class LunarServletContextListener implements ServletContextListener {
	private final static Logger LOG = LoggerFactory.getLogger(LunarServletContextListener.class);

	@Override
	public void contextInitialized(final ServletContextEvent sce) {
		final Scheduler defaultScheduler = ComponentFactory.getLogicComponent().defaultScheduler();
		try {
			defaultScheduler.start();
		}
		catch (final SchedulerException e) {
			LOG.error("failed to start quartz", e);
			throw new RuntimeException("failed to start scheduler", e);
		}
	}

	@Override
	public void contextDestroyed(final ServletContextEvent sce) {
		final Scheduler defaultScheduler = ComponentFactory.getLogicComponent().defaultScheduler();
		final ILunarDatabaseManager ldm = ComponentFactory.getDatabaseComponent().iLunarDatabaseManager();
		try {
			defaultScheduler.pauseAll();
		}
		catch (final SchedulerException e) {
			LOG.error("failed to pause scheduler", e);
		}
		ldm.shutdown();
		try {
			defaultScheduler.shutdown(true);
		}
		catch (final SchedulerException e) {
			LOG.error("failed to shutdown quartz", e);
			throw new RuntimeException("failed to shutdown scheduler", e);
		}
	}
}