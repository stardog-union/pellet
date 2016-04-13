package com.clarkparsia.pellet.server.handlers;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * @author Evren Sirin
 */
public class ReasonerUpdateHandler extends AbstractRoutingHandler {
	private static final Logger LOGGER = Logger.getLogger(ReasonerUpdateHandler.class.getName());
	private final boolean insert;

	public ReasonerUpdateHandler(final ServerState theServerState,
	                             final boolean insert) {
		super("POST", "{ontology}/" + (insert ? "insert" : "delete"), theServerState);

		this.insert = insert;
	}

	@Override
	public void handleRequest(final HttpServerExchange theExchange) throws Exception {
		final IRI ontology = getOntology(theExchange);
		final UUID clientId = getClientID(theExchange);

		final SchemaReasoner aReasoner = getReasoner(ontology, clientId);

		final Set<OWLAxiom> axioms = readAxioms(theExchange.getInputStream());

		LOGGER.info("Updating client " + clientId + " (+" + axioms.size() + ")");

		if (insert) {
			aReasoner.insert(axioms);
		}
		else {
			aReasoner.delete(axioms);
		}

		LOGGER.info("Updating client " + clientId + " Success!");

		theExchange.setStatusCode(StatusCodes.OK);
		theExchange.endExchange();
	}
}
