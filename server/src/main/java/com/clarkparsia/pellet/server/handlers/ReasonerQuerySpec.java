package com.clarkparsia.pellet.server.handlers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.json.GenericJsonMessage;
import com.google.inject.Inject;
import io.undertow.Handlers;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.PathTemplateMatch;
import io.undertow.util.StatusCodes;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ReasonerQuerySpec extends ReasonerSpec {

	@Inject
	public ReasonerQuerySpec(final ServerState theServerState) {
		super(theServerState);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPath() {
		return path("{ontology}/query");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HttpHandler getHandler() {
		return Handlers.predicate(Predicates.parse("method(POST)"),
		                          new ReasonerQueryHandler(mServerState),
		                          new MethodNotAllowedHandler("POST"));
	}

	@Override
	public PathType getPathType() {
		return PathType.TEMPLATE;
	}

	static class ReasonerQueryHandler implements HttpHandler {

		private final ServerState serverState;

		public ReasonerQueryHandler(final ServerState theServerState) {
			serverState = theServerState;
		}

		@Override
		public void handleRequest(final HttpServerExchange theHttpServerExchange) throws Exception {
			String ontology = URLDecoder.decode(theHttpServerExchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY)
			                                                         .getParameters().get("ontology"),
			                                    StandardCharsets.UTF_8.name());
			GenericJsonMessage aMessage = new GenericJsonMessage("Doing reasoning query on ontology: " + ontology + " - oohhmm...");

			theHttpServerExchange.setResponseCode(StatusCodes.OK);
			theHttpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, aMessage.getMimeType());
			theHttpServerExchange.getResponseSender().send(aMessage.toJsonString());
			// TODO: implement using a ClientState -> schema reasoner
		}
	}
}
