package com.clarkparsia.pellet.server.handlers;

import com.clarkparsia.pellet.server.model.ServerState;
import com.complexible.pellet.service.messages.GenericJsonMessage;
import com.google.inject.Inject;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ReasonerUpdateSpec extends ReasonerSpec {

	@Inject
	public ReasonerUpdateSpec(final ServerState theServerState) {
		super(theServerState);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPath() {
		return path("update");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HttpHandler getHandler() {
		return new ReasonerUpdateHandler(mServerState);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isExactPath() {
		return false;
	}

	static class ReasonerUpdateHandler implements HttpHandler {

		private final ServerState mStateServer;

		public ReasonerUpdateHandler(final ServerState theServerState) {
			mStateServer = theServerState;
		}

		@Override
		public void handleRequest(final HttpServerExchange theHttpServerExchange) throws Exception {
			GenericJsonMessage aMessage = new GenericJsonMessage("Doing reasoning update hmm...");

			theHttpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, aMessage.getMimeType());
			theHttpServerExchange.getResponseSender().send(aMessage.toJsonString());
			// TODO: implement using a ClientState -> schema reasoner
		}
	}
}
