package com.clarkparsia.pellet.server.handlers;

import com.clarkparsia.pellet.server.PelletServer;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.util.Headers;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ServerShutdownHandler implements HttpHandler {

	private final PelletServer mPelletServer;
	private final GracefulShutdownHandler mGracefulShutdownHandler;

	private ServerShutdownHandler(final PelletServer thePelletServer,
	                              final GracefulShutdownHandler theGracefulShutdownHandler) {
		mPelletServer = thePelletServer;
		mGracefulShutdownHandler = theGracefulShutdownHandler;
	}

	@Override
	public void handleRequest(final HttpServerExchange exchange) throws Exception {
		// start closing connections and stop accepting more
		mGracefulShutdownHandler.shutdown();

		// trigger server stop when everything is shutdown
		mGracefulShutdownHandler.addShutdownListener(new GracefulShutdownHandler.ShutdownListener() {
			@Override
			public void shutdown(final boolean isDown) {
				if (isDown) {
					mPelletServer.stop();
				}
			}
		});

		exchange.endExchange();
	}

	public static HttpHandler newInstance(final PelletServer thePelletServer,
	                                      final GracefulShutdownHandler theGracefulShutdownHandler) {
		return new ServerShutdownHandler(thePelletServer, theGracefulShutdownHandler);
	}

}
