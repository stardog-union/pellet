package com.clarkparsia.pellet.server;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.handlers.PathHandlerSpec;
import com.clarkparsia.pellet.server.handlers.ServerShutdownHandler;
import com.clarkparsia.pellet.server.jobs.ServerStateReload;
import com.clarkparsia.pellet.server.model.ServerState;
import com.google.inject.Injector;
import com.google.inject.Key;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.ExceptionHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.PathTemplateHandler;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import com.clarkparsia.pellet.server.ConfigurationReader.PelletSettings;

/**
 * Pellet PelletServer implementation with Undertow.
 *
 * @author Edgar Rodriguez-Diaz
 * @see <a href="http://undertow.io">undertow.io</a>
 */
public final class PelletServer {

	private static final Logger LOGGER = Logger.getLogger(PelletServer.class.getName());

	public static final String DEFAULT_HOST = "localhost";
	public static final int DEFAULT_PORT = 18080;

	public static final String ROOT_PATH = "/";

	private Undertow server;
	private boolean isRunning = false;

	private final Injector serverInjector;

	private Scheduler jobScheduler;

	public PelletServer(final Injector theInjector) {
		serverInjector = theInjector;
	}

	public void start() throws ServerException {

		final Set<PathHandlerSpec> pathSpecs = serverInjector.getInstance(Key.get(PelletServerModule.PATH_SPECS));

		// Servlets are attached to ROOT_PATH
		final PathHandler path = Handlers.path(Handlers.redirect(ROOT_PATH));
		final PathTemplateHandler pathTemplates = new PathTemplateHandler(path);

		for (PathHandlerSpec spec : pathSpecs) {
			switch (spec.getPathType()) {
				case PREFIX:
					path.addPrefixPath(spec.getPath(), spec.getHandler());
					break;
				case TEMPLATE:
					pathTemplates.add(spec.getPath(), spec.getHandler());
					break;
				default:
					path.addExactPath(spec.getPath(), spec.getHandler());
			}
		}

		// Exceptions handler
		final ExceptionHandler aExceptionHandler = Handlers.exceptionHandler(pathTemplates);

		// Shutdown handler
		final GracefulShutdownHandler aShutdownHandler = Handlers.gracefulShutdown(aExceptionHandler);

		// add shutdown path
		path.addExactPath("/admin/shutdown", ServerShutdownHandler.newInstance(this, aShutdownHandler));

		final ConfigurationReader aConfig = ConfigurationReader.of(serverInjector.getInstance(Configuration.class));

		final PelletSettings aPelletSettings = aConfig.pelletSettings();

		server = Undertow.builder()
		                 .addHttpListener(aPelletSettings.port(), aPelletSettings.host())
		                 .setServerOption(UndertowOptions.ALWAYS_SET_DATE, true)
		                 .setHandler(aShutdownHandler)
		                 .build();


		System.out.println(String.format("Pellet Home: %s", aPelletSettings.home()));
		System.out.println(String.format("Listening at: http://%s:%s", aPelletSettings.host(), aPelletSettings.port()));

		isRunning = true;
		server.start();

		try {
			startJobs(aPelletSettings);
		}
		catch (SchedulerException se) {
			throw new ServerException(500, se);
		}
	}

	private void startJobs(PelletSettings aPelletSettings) throws SchedulerException {
		final JobDataMap jobData = new JobDataMap();
		final int updateIntervalSec = aPelletSettings.updateIntervalInSeconds();

		jobData.put("ServerState", this.getState());

		final JobDetail stateFetch = JobBuilder.newJob(ServerStateReload.class)
		                                       .usingJobData(jobData)
		                                       .withIdentity("serverStateFetch")
		                                       .build();

		final SimpleTrigger trigger = TriggerBuilder.newTrigger()
		                                            .withIdentity("everyNsecs")
		                                            .startNow()
		                                            .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(updateIntervalSec))
		                                            .build();

		LOGGER.info("Starting Job Scheduler for Updates every "+ updateIntervalSec +" seconds");

		jobScheduler = StdSchedulerFactory.getDefaultScheduler();
		jobScheduler.scheduleJob(stateFetch, trigger);
		jobScheduler.start();
	}

	public ServerState getState() {
		return serverInjector.getInstance(ServerState.class);
	}

	public void stop() {
		if (server != null && isRunning) {
			System.out.println("Received request to shutdown");
			System.out.println("System is shutting down...");

			try {
				// stop job scheduler fetching server state from Protege
				jobScheduler.shutdown();

				// invalidate ServerState
				serverInjector.getInstance(ServerState.class)
				              .close();
			}
			catch (Exception e) {
				LOGGER.log(Level.FINER, "Error while stopping the job scheduler", e);;
			}

			server.stop();
			server = null;
			isRunning = false;
		}
	}
}
