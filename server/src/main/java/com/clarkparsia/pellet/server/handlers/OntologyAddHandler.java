package com.clarkparsia.pellet.server.handlers;

import java.util.Set;

import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.google.inject.Inject;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class OntologyAddHandler extends AbstractRoutingHandler {

	@Inject
	public OntologyAddHandler(final ServerState theServerState,
	                          final Set<ServiceDecoder> theDecoders,
	                          final Set<ServiceEncoder> theEncoders) {
		super("PUT", "{ontology}", theServerState, theEncoders, theDecoders);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleRequest(final HttpServerExchange theExchange) throws Exception {
		getServerState().addOntology(getOntology(theExchange).toString());

		theExchange.setStatusCode(StatusCodes.OK);
		theExchange.endExchange();
	}
}
