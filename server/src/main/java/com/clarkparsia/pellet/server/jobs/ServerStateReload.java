package com.clarkparsia.pellet.server.jobs;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.clarkparsia.pellet.server.model.ServerState;
import com.google.common.base.Stopwatch;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Quartz Job definition for reloading Server State.
 *
 * @author Edgar Rodriguez-Diaz
 */
public final class ServerStateReload implements Job {

	private static final Logger LOGGER = Logger.getLogger(ServerStateReload.class.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final JobExecutionContext jobContext) throws JobExecutionException {
		final ServerState serverState = (ServerState) jobContext.getJobDetail()
		                                                        .getJobDataMap()
		                                                        .get("ServerState");

		LOGGER.info("Checking for updated ontologies...");
		boolean updated = serverState.update();
		if (updated) {
			LOGGER.info("Ontology updates are complete");
		}
		else {
			LOGGER.info("No ontologies were updated");
		}

	}
}