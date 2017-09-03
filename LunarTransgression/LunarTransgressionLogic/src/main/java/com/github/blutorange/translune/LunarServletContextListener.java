package com.github.blutorange.translune;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.job.CleanInitId;
import com.github.blutorange.translune.job.SaveDb;
import com.github.blutorange.translune.util.CustomProperties;

@WebListener(value = "Listener for the lunar application")
public class LunarServletContextListener implements ServletContextListener {
	@Inject
	@Classed(LunarServletContextListener.class)
	Logger logger;

	@Inject
	@Named("default")
	Scheduler scheduler;

	@Inject
	CustomProperties customProperties;

	public void initialize() {
		ComponentFactory.getLunarComponent().inject(this);
		try {
			scheduler.start();
			addJobSaveDb(scheduler);
			addJobCleanInitId(scheduler);
		}
		catch (final SchedulerException e) {
			logger.error("failed to start quartz", e);
			throw new RuntimeException("failed to start scheduler", e);
		}
	}

	@Override
	public void contextInitialized(final ServletContextEvent sce) {
		initialize();
	}

	private void addJobCleanInitId(final Scheduler scheduler) throws SchedulerException {
		logger.debug("adding job cleanInitId");
		final JobDetail jobDetail = JobBuilder.newJob(CleanInitId.class).withIdentity("cleanInitId", "clean")
				.withDescription("Removes expired initiation tokens.").build();
		final SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(12)
				.repeatForever();
		final Trigger jobTrigger = TriggerBuilder.newTrigger().withIdentity("triggerCleanInitId", "clean").startNow()
				.withSchedule(scheduleBuilder).build();
		scheduler.scheduleJob(jobDetail, jobTrigger);
	}

	private void addJobSaveDb(final Scheduler scheduler) throws SchedulerException {
		logger.debug("adding job saveDb");
		final JobDetail jobDetail = JobBuilder.newJob(SaveDb.class).withIdentity("jobSaveDb", "db")
				.withDescription("Writes local unsaved changes to the database.").build();
		final SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
				.withIntervalInMinutes(customProperties.getDatabaseSaveMinutes()).repeatForever();
		final Trigger jobTrigger = TriggerBuilder.newTrigger().withIdentity("triggerSaveDb", "db").startNow()
				.withSchedule(scheduleBuilder).build();
		scheduler.scheduleJob(jobDetail, jobTrigger);
	}

	public void destroy() {
		final Scheduler defaultScheduler = ComponentFactory.getLunarComponent().defaultScheduler();
		final ILunarDatabaseManager ldm = ComponentFactory.getLunarComponent().iLunarDatabaseManager();
		try {
			defaultScheduler.pauseAll();
		}
		catch (final SchedulerException e) {
			logger.error("failed to pause scheduler", e);
		}
		ldm.shutdown();
		try {
			final EntityManagerFactory emf = ComponentFactory.getLunarComponent().entityManagerFactory().get();
			if (emf.isOpen())
				emf.close();
		}
		catch (final Exception e) {
			logger.error("failed to close entity manager factory", e);
		}
		try {
			defaultScheduler.shutdown(true);
		}
		catch (final SchedulerException e) {
			logger.error("failed to shutdown quartz", e);
			throw new RuntimeException("failed to shutdown scheduler", e);
		}
	}

	@Override
	public void contextDestroyed(final ServletContextEvent sce) {
		destroy();
	}
}