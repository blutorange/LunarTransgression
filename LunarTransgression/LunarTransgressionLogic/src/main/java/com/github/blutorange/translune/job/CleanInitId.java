package com.github.blutorange.translune.job;

import javax.inject.Inject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.logic.IInitIdStore;

public class CleanInitId implements Job {
	private final Logger logger = LoggerFactory.getLogger(CleanInitId.class);

	@Inject
	IInitIdStore initIdStore;

	public CleanInitId() {
		ComponentFactory.getLunarComponent().inject(this);
	}

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		logger.info("running job: clean init ID");
		try {
			initIdStore.cleanExpired();
		}
		catch (final Exception e) {
			logger.error("failed to clean the init ID store", e);
			throw new JobExecutionException("failed to clean the init ID store", e);
		}
	}
}