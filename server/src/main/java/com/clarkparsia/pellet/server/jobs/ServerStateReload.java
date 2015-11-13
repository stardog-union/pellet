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

		LOGGER.info("Reloading server state on " + jobContext.getFireTime().toString());
		final Stopwatch aWatch = Stopwatch.createStarted();
		serverState.update();
		aWatch.stop();
		LOGGER.info("Done, server state reloaded in " +
		            aWatch.elapsed(TimeUnit.MILLISECONDS) + " miliseconds");
	}
}