package com.clarkparsia.pellet.server;

import java.io.File;
import java.util.Set;

import javax.servlet.ServletException;

import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.handlers.PathHandlerSpec;
import com.clarkparsia.pellet.server.handlers.PelletExceptionHandler;
import com.clarkparsia.pellet.server.handlers.ServerShutdownHandler;
import com.clarkparsia.pellet.server.protege.ProtegeServerConfiguration;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.ExceptionHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.PathTemplateHandler;

/**
 * Pellet PelletServer implementation with Undertow.
 *
 * @author Edgar Rodriguez-Diaz
 * @see <a href="http://undertow.io">undertow.io</a>
 */
public final class PelletServer {

	private static final String HOST = "localhost";
	private static final int PORT = 8080;

	public static final String ROOT_PATH = "/";

	private Undertow server;
	private boolean isRunning = false;

	private final Injector serverInjector;

	public PelletServer(final Injector theInjector) {
		serverInjector = theInjector;
	}

	public void start() throws ServletException {

		Set<PathHandlerSpec> pathSpecs = serverInjector.getInstance(Key.get(PelletServerModule.PATH_SPECS));

		// Servlets are attached to ROOT_PATH
		PathHandler path = Handlers.path(Handlers.redirect(ROOT_PATH));
		PathTemplateHandler pathTemplates = new PathTemplateHandler(path);

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
		ExceptionHandler aExceptionHandler = Handlers.exceptionHandler(pathTemplates);

		// Shutdown handler
		GracefulShutdownHandler aShutdownHandler = Handlers.gracefulShutdown(aExceptionHandler);

		// add shutdown path
		path.addExactPath("/admin/shutdown", ServerShutdownHandler.newInstance(this, aShutdownHandler));

		server = Undertow.builder()
		                 .addHttpListener(PORT, HOST)
		                 .setServerOption(UndertowOptions.ALWAYS_SET_DATE, true)
		                 .setHandler(aShutdownHandler)
		                 .build();


		System.out.println(String.format("Listening at: http://%s:%s", HOST, PORT));

		isRunning = true;
		server.start();
	}

	public void stop() {
		if (server != null && isRunning) {
			System.out.println("Received request to shutdown");
			System.out.println("System is shutting down...");

			server.stop();
			isRunning = false;
		}
	}
}
