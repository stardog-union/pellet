package com.clarkparsia.pellet.server.handlers;

import java.io.StringWriter;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.owlapi.explanation.io.manchester.ManchesterSyntaxExplanationRenderer;
import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import io.undertow.server.HttpServerExchange;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Specification for {@link SchemaReasoner#explain(OWLAxiom, int)} functionality within
 * the Pellet Server.
 *
 * @author Edgar Rodriguez-Diaz
 */
public class ReasonerExplainHandler extends AbstractRoutingHandler {
	private static final Logger LOGGER = Logger.getLogger(ReasonerExplainHandler.class.getName());

	@Inject
	public ReasonerExplainHandler(final ServerState theServerState) {
		super("POST", "{ontology}/explain", theServerState);
	}

	@Override
	public void handleRequest(final HttpServerExchange theExchange) throws Exception {
		final IRI ontology = getOntology(theExchange);
		final UUID clientId = getClientID(theExchange);

		int limit = getLimit(theExchange);

		OWLAxiom inference = readAxiom(theExchange.getInputStream());

		final SchemaReasoner aReasoner = getReasoner(ontology, clientId);
		final Set<Set<OWLAxiom>> explanations = aReasoner.explain(inference, limit);

		if (LOGGER.isLoggable(Level.INFO)) {
			StringWriter sw = new StringWriter();
			ManchesterSyntaxExplanationRenderer renderer = new ManchesterSyntaxExplanationRenderer();
			renderer.startRendering(sw);
			renderer.render(inference, explanations);
			renderer.endRendering();
			LOGGER.info(sw.toString());
		}

		OWLDataFactory factory = manager.getOWLDataFactory();
		Set<OWLAxiom> axioms = Sets.newHashSet();
		int i = 1;
		for (Set<OWLAxiom> explanation : explanations) {
			OWLAnnotation annotation = factory.getOWLAnnotation(factory.getRDFSLabel(), factory.getOWLLiteral("explanation" + (i++)));
			for (OWLAxiom axiom : explanation) {
				axioms.add(axiom.getAnnotatedAxiom(ImmutableSet.of(annotation)));
			}
		}

		OWLOntology ont = manager.createOntology();
		manager.addAxioms(ont, axioms);

		manager.saveOntology(ont, theExchange.getOutputStream());

		theExchange.endExchange();
	}

	private int getLimit(final HttpServerExchange theExchange) throws ServerException {
		int limit = 0;
		try {
			limit = Integer.parseInt(getQueryParameter(theExchange, "limit"));
		}
		catch (Exception e) {
			throwBadRequest("Query 'limit' parameter is not valid, it must be an Integer.");
		}

		return limit;
	}
}
