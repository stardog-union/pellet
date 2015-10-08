package com.clarkparsia.pellet.server.handlers;

import com.clarkparsia.pellet.server.Server;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.util.Headers;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ServerShutdownHandler implements HttpHandler {

	private final Server mServer;
	private final GracefulShutdownHandler mGracefulShutdownHandler;

	private ServerShutdownHandler(final Server theServer,
	                              final GracefulShutdownHandler theGracefulShutdownHandler) {
		mServer = theServer;
		mGracefulShutdownHandler = theGracefulShutdownHandler;
	}

	@Override
	public void handleRequest(final HttpServerExchange exchange) throws Exception {
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

		exchange.getResponseSender().send("{ \"message\": \"Server is shutting down.\" }");

		// start closing connections and stop accepting more
		mGracefulShutdownHandler.shutdown();

		// trigger server stop when everything is shutdown
		mGracefulShutdownHandler.addShutdownListener(new GracefulShutdownHandler.ShutdownListener() {
			@Override
			public void shutdown(final boolean isDown) {
				if (isDown) {
					mServer.stop();
				}
			}
		});
	}

	public static HttpHandler newInstance(final Server theServer,
	                                      final GracefulShutdownHandler theGracefulShutdownHandler) {
		return new ServerShutdownHandler(theServer, theGracefulShutdownHandler);
	}

}
