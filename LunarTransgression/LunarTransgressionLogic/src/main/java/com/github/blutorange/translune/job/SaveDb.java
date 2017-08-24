package com.github.blutorange.translune.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.ic.ComponentFactory;

public class SaveDb implements Job {
	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
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