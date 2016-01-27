package com.clarkparsia.pellet.server.handlers;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.clarkparsia.pellet.service.messages.UpdateRequest;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import org.semanticweb.owlapi.model.IRI;

/**
 * Specification for {@link SchemaReasoner#update(Set, Set)} functionality within
 * the Pellet Server.
 *
 * @author Edgar Rodriguez-Diaz
 */
public class ReasonerUpdateHandler extends AbstractRoutingHandler {
	private static final Logger LOGGER = Logger.getLogger(ReasonerUpdateHandler.class.getName());

	@Inject
	public ReasonerUpdateHandler(final ServerState theServerState,
	                             final Set<ServiceDecoder> theDecoders,
	                             final Set<ServiceEncoder> theEncoders) {
		super("POST", "{ontology}", theServerState, theEncoders, theDecoders);
	}

	@Override
	public void handleRequest(final HttpServerExchange theExchange) throws Exception {
		final IRI ontology = getOntology(theExchange);
		final UUID clientId = getClientID(theExchange);

		byte[] inBytes = readInput(theExchange.getInputStream(), false /* don't fail on empty input */);

		if (inBytes.length == 0) {
			// If there's no payload we finish the exchange
			theExchange.setStatusCode(StatusCodes.OK);
			theExchange.endExchange();
			return;
		}

		final ServiceDecoder decoderOpt = getDecoder(getContentType(theExchange));

		final UpdateRequest aUpdateRequest = decoderOpt.updateRequest(inBytes);

		final SchemaReasoner aReasoner = getReasoner(ontology, clientId);

		LOGGER.info("Updating client " + clientId +
		            " (+" + aUpdateRequest.getAdditions().size() + ", -" + aUpdateRequest.getAdditions().size() + ")");

		aReasoner.update(aUpdateRequest.getAdditions(), aUpdateRequest.getRemovals());

		LOGGER.info("Updating client " + clientId + " Success!");

		theExchange.setStatusCode(StatusCodes.OK);

		theExchange.endExchange();
	}
}
