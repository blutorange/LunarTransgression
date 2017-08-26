package com.github.blutorange.translune.job;

import javax.inject.Inject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.ic.ComponentFactory;

public class SaveDb implements Job {
	private final static Logger LOG = LoggerFactory.getLogger(SaveDb.class);

	@Inject
	ILunarDatabaseManager ldm;

	public SaveDb() {
		ComponentFactory.getLunarComponent().inject(this);
	}

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		LOG.info("running job saveDb");
		try {
			ldm.runPeriodically();
		}
		catch (final Exception e) {
			LOG.error("failed to flush data to database", e);
			throw new JobExecutionException("failed to flush data to database", e);
		}
	}
}