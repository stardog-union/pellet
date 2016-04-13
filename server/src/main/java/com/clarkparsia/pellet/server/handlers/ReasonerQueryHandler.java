package com.clarkparsia.pellet.server.handlers;

import java.util.UUID;

import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.messages.JsonMessage;
import com.clarkparsia.pellet.service.reasoner.SchemaQuery;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.inject.Inject;
import io.undertow.server.HttpServerExchange;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * Specification for {@link SchemaReasoner#query(SchemaQuery)} functionality within
 * the Pellet Server.
 *
 * @author Edgar Rodriguez-Diaz
 */
public class ReasonerQueryHandler extends AbstractRoutingHandler {
	@Inject
	public ReasonerQueryHandler(final ServerState theServerState) {
		super("POST", "{ontology}/query", theServerState);
	}

	@Override
	public void handleRequest(final HttpServerExchange theExchange) throws Exception {
		final IRI ontology = getOntology(theExchange);
		final UUID clientId = getClientID(theExchange);
		final SchemaQuery query = JsonMessage.readQuery(theExchange.getInputStream());
		final SchemaReasoner aReasoner = getReasoner(ontology, clientId);
		final NodeSet<? extends OWLObject> result = aReasoner.query(query);

		JsonMessage.writeNodeSet(result, theExchange.getOutputStream());

		theExchange.endExchange();
	}
}
