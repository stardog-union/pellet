package com.clarkparsia.pellet.server.handlers;

import com.google.common.base.Joiner;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class MethodNotAllowedHandler implements HttpHandler {
	final String[] allowed;

	public MethodNotAllowedHandler(final String... theAllowed) {
		allowed = theAllowed;
	}

	@Override
	public void handleRequest(final HttpServerExchange theHttpServerExchange) throws Exception {
		theHttpServerExchange.setResponseCode(StatusCodes.METHOD_NOT_ALLOWED);
		theHttpServerExchange.getResponseHeaders().put(Headers.ALLOW, Joiner.on(",").join(allowed));

		// finish the exchange and let undertow take care of the rest
		theHttpServerExchange.endExchange();
	}
}
