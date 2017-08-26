package com.github.blutorange.translune.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Transient;

import org.primefaces.component.inputswitch.InputSwitch;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.serial.IImportProcessing;
import com.github.blutorange.translune.util.CustomProperties;

@ManagedBean(eager = true, name = "statusBean")
@ViewScoped
public class StatusBean extends AbstractBean {
	@SuppressWarnings("hiding")
	@Transient
	@Inject
	@Classed(StatusBean.class)
	Logger logger;

	@Transient
	@Inject
	IImportProcessing importProcessing;

	@Transient
	@Inject
	CustomProperties customProperties;

	@Transient
	@Inject
	@Named("default")
	Scheduler scheduler;

	@Transient
	@Inject
	ILunarDatabaseManager databaseManager;

	@PostConstruct
	void init() {
		ComponentFactory.getLunarComponent().inject(this);
	}

	/**
	 * @return the online
	 */
	public boolean isOnline() {
		return customProperties.isOnline();
	}

	/**
	 * @param actionEvent
	 */
	public void databaseCheck(final ActionEvent actionEvent) {
		try {
			databaseManager.checkConnection();
			final FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Database connection attempt successful", "");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		catch (final Exception e) {
			logger.error("Could not connect to the database", e);
			final FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Failed to connect to the database", e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public List<JobEntry> getQuartzJobs() {
		final List<JobEntry> jobEntries = new ArrayList<>();
		try {
			for (final JobKey jobKey : scheduler.getJobKeys(GroupMatcher.anyGroup())) {
				String description;
				final List<Trigger> triggers = new ArrayList<>();
				try {
					description = scheduler.getJobDetail(jobKey).getDescription();
				}
				catch (final SchedulerException e) {
					logger.error("failed to get job description for " + jobKey.getName(), e);
					description = "No description found";
				}
				try {
					triggers.addAll(scheduler.getTriggersOfJob(jobKey));
				}
				catch (final SchedulerException e) {
					logger.error("failed to get job triggers for " + jobKey.getName(), e);
				}
				for (final Trigger trigger : triggers) {
					jobEntries.add(new JobEntry(jobKey.getName(), jobKey.getGroup(), description, trigger.getPriority(),
							trigger.getPreviousFireTime(), trigger.getNextFireTime()));
				}

			}
		}
		catch (final SchedulerException e) {
			logger.error("failed to get jobs", e);
		}
		return jobEntries;
	}

	public void onOnlineChange(final AjaxBehaviorEvent event) {
		final InputSwitch inputSwitch = (InputSwitch) event.getComponent();
		setOnline(inputSwitch.isDisabled());
	}

	/**
	 * @param online
	 *            the online to set
	 */
	public void setOnline(final boolean online) {
		customProperties.setOnline(online);
	}
}