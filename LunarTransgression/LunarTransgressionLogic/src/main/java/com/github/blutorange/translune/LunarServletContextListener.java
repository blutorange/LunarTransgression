package com.github.blutorange.translune;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.job.SaveDb;
import com.github.blutorange.translune.util.Constants;

@WebListener(value="Listener for the lunar application")
public class LunarServletContextListener implements ServletContextListener {
	private final static Logger LOG = LoggerFactory.getLogger(LunarServletContextListener.class);

	public void initialize() {
		final Scheduler defaultScheduler = ComponentFactory.getLogicComponent().defaultScheduler();
		try {
			defaultScheduler.start();
			addJobSaveDb(defaultScheduler);
		}
		catch (final SchedulerException e) {
			LOG.error("failed to start quartz", e);
			throw new RuntimeException("failed to start scheduler", e);
		}
	}

	@Override
	public void contextInitialized(final ServletContextEvent sce) {
		initialize();
	}

	private void addJobSaveDb(final Scheduler scheduler) throws SchedulerException {
		LOG.debug("adding job saveDb");
		final JobDetail jobDetail = JobBuilder.newJob(SaveDb.class).withIdentity("jobSaveDb", "db").build();
		final ScheduleBuilder<SimpleTrigger> scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
				.withIntervalInMinutes(Constants.CONFIG_DATABASE_SAVE_MINUTES).repeatForever();
		final Trigger jobTrigger = TriggerBuilder.newTrigger().withIdentity("triggerSaveDb", "db").startNow()
				.withSchedule(scheduleBuilder).build();
		scheduler.scheduleJob(jobDetail, jobTrigger);
	}

	public void destroy() {
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

	@Override
	public void contextDestroyed(final ServletContextEvent sce) {
		destroy();
	}
}