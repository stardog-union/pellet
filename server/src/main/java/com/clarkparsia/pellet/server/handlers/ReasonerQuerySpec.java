package com.clarkparsia.pellet.server.handlers;

import com.clarkparsia.pellet.server.PelletServer;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ReasonerQuerySpec implements PathHandlerSpec {

	@Override
	public String getPath() {
		return PelletServer.ROOT_PATH + "/reasoner/query";
	}

	@Override
	public HttpHandler getHandler() {
		return new ReasonerQueryHandler();
	}

	@Override
	public boolean isExactPath() {
		return true;
	}

	static class ReasonerQueryHandler implements HttpHandler {

		@Override
		public void handleRequest(final HttpServerExchange theHttpServerExchange) throws Exception {
			// Implement reasoner
		}
	}
}
