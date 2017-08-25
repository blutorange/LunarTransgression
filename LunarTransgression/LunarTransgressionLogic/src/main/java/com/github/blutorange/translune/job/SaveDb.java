package com.github.blutorange.translune.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.ic.ComponentFactory;

public class SaveDb implements Job {
	private final static Logger LOG = LoggerFactory.getLogger(SaveDb.class);
	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		LOG.info("running job saveDb");
		ILunarDatabaseManager ldm;
		try {
			ldm = ComponentFactory.getDatabaseComponent().iLunarDatabaseManager();
		}
		catch (final ClassCastException e) {
			throw new JobExecutionException("failed to acquire lunar database manager", e);
		}
		ldm.runPeriodically();
	}
}