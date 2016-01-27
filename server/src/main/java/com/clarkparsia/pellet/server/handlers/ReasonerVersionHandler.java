package com.clarkparsia.pellet.server.handlers;

import java.util.Set;
import java.util.UUID;

import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.google.common.net.MediaType;
import com.google.inject.Inject;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ReasonerVersionHandler extends AbstractRoutingHandler {

	@Inject
	public ReasonerVersionHandler(final ServerState theServerState,
	                              final Set<ServiceDecoder> theDecoders,
	                              final Set<ServiceEncoder> theEncoders) {
		super("GET", "{ontology}/version", theServerState, theEncoders, theDecoders);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleRequest(final HttpServerExchange theExchange) throws Exception {
		// TODO: Is this the best way to identify the client?
		final UUID clientId = getClientID(theExchange);

		// Get local client reasoner's version
		int version = getReasoner(getOntology(theExchange), clientId).version();

		theExchange.setStatusCode(StatusCodes.OK);
		theExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, MediaType.PLAIN_TEXT_UTF_8.toString());
		theExchange.getResponseSender().send(String.valueOf(version));
		theExchange.endExchange();
	}
}
