package com.clarkparsia.pellet.server.handlers;

import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.json.GenericJsonMessage;
import com.google.inject.Inject;
import io.undertow.Handlers;
import io.undertow.predicate.Predicates;
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
		return path("{ontology}");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HttpHandler getHandler() {
		return Handlers.predicate(Predicates.parse("method(PUT)"),
		                          new ReasonerUpdateHandler(mServerState),
		                          new MethodNotAllowedHandler("PUT"));
	}

	@Override
	public PathType getPathType() {
		return PathType.TEMPLATE;
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
