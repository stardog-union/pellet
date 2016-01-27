package com.clarkparsia.pellet.server.handlers;

import java.util.Set;

import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.google.inject.Inject;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import org.semanticweb.owlapi.model.IRI;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class OntologyRemoveHandler extends AbstractRoutingHandler {

	@Inject
	public OntologyRemoveHandler(final ServerState theServerState,
	                             final Set<ServiceDecoder> theDecoders,
	                             final Set<ServiceEncoder> theEncoders) {
		super("DELETE", "{ontology}", theServerState, theEncoders, theDecoders);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleRequest(final HttpServerExchange theExchange) throws Exception {
		IRI ont = getOntology(theExchange);
		boolean removed = getServerState().removeOntology(ont);

		if (removed) {
			theExchange.setStatusCode(StatusCodes.OK);
		}
		else {
			theExchange.setStatusCode(StatusCodes.BAD_REQUEST);
			theExchange.setReasonPhrase("Ontology not found: " + ont);
		}
		theExchange.endExchange();
	}
}
