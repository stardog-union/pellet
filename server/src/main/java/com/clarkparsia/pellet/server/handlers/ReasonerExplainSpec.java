package com.clarkparsia.pellet.server.handlers;

import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.json.GenericJsonMessage;
import com.google.inject.Inject;
import io.undertow.Handlers;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ReasonerExplainSpec extends ReasonerSpec {

	@Inject
	public ReasonerExplainSpec(final ServerState theServerState) {
		super(theServerState);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPath() {
		return path("{ontology}/explain");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HttpHandler getHandler() {
		return Handlers.predicate(Predicates.parse("method(POST)"),
		                          new ReasonerExplainHandler(mServerState),
		                          new MethodNotAllowedHandler("POST"));
	}

	@Override
	public PathType getPathType() {
		return PathType.TEMPLATE;
	}

	static class ReasonerExplainHandler implements HttpHandler {

		private final ServerState serverState;

		public ReasonerExplainHandler(final ServerState theServerState) {
			serverState = theServerState;
		}

		@Override
		public void handleRequest(final HttpServerExchange theHttpServerExchange) throws Exception {
			GenericJsonMessage aMessage = new GenericJsonMessage("Doing reasoning explain hmm...");

			theHttpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, aMessage.getMimeType());
			theHttpServerExchange.getResponseSender().send(aMessage.toJsonString());

			// TODO: implement using a ClientState -> schema reasoner
		}
	}
}
