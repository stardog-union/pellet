package com.clarkparsia.pellet.server;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.pellet.server.ConfigurationReader.PelletSettings;
import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.handlers.RoutingHandler;
import com.clarkparsia.pellet.server.jobs.ServerStateUpdate;
import com.clarkparsia.pellet.server.model.ServerState;
import com.google.inject.Injector;
import com.google.inject.Key;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.ExceptionHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;

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

	private ScheduledExecutorService jobScheduler;

	public PelletServer(final Injector theInjector) {
		serverInjector = theInjector;
	}

	public void start() throws ServerException {
		final Set<RoutingHandler> aHandlers = serverInjector.getInstance(Key.get(PelletServerModule.HANDLERS));

		// Routing handler
		final io.undertow.server.RoutingHandler router = Handlers.routing();

		// Exceptions handler
		final ExceptionHandler aExceptionHandler = Handlers.exceptionHandler(router);

		// Shutdown handler
		final GracefulShutdownHandler aShutdownHandler = Handlers.gracefulShutdown(aExceptionHandler);

		for (RoutingHandler spec : aHandlers) {
			// Since we're doing IO in the Handlers, we have to wrap them in a BlockingHandler
			final BlockingHandler aHandler = new BlockingHandler(spec);
			router.add(spec.getMethod(), spec.getPath(), aHandler);
		}

		// add shutdown path
		router.add("GET", "/admin/shutdown", new HttpHandler() {
			@Override
			public void handleRequest(final HttpServerExchange exchange) throws Exception {
				aShutdownHandler.shutdown();
				aShutdownHandler.addShutdownListener(new GracefulShutdownHandler.ShutdownListener() {
					@Override
					public void shutdown(final boolean isDown) {
						if (isDown) {
							stop();
						}
					}
				});
				exchange.endExchange();
			}
		});

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

		startJobs(aPelletSettings);
	}

	private void startJobs(PelletSettings aPelletSettings) {
		final int updateIntervalSec = aPelletSettings.updateIntervalInSeconds();

		LOGGER.info("Starting Job Scheduler for Updates every "+ updateIntervalSec +" seconds");

		jobScheduler = Executors.newScheduledThreadPool(1);
		jobScheduler.scheduleAtFixedRate(new ServerStateUpdate(getState()), updateIntervalSec, updateIntervalSec, TimeUnit.SECONDS);
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
				getState().close();
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
