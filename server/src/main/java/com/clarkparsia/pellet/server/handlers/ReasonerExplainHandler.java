package com.clarkparsia.pellet.server.handlers;

import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.UUID;

import com.clarkparsia.owlapi.explanation.io.manchester.ManchesterSyntaxExplanationRenderer;
import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.clarkparsia.pellet.service.messages.ExplainRequest;
import com.clarkparsia.pellet.service.messages.ExplainResponse;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Specification for {@link SchemaReasoner#explain(OWLAxiom, int)} functionality within
 * the Pellet Server.
 *
 * @author Edgar Rodriguez-Diaz
 */
public class ReasonerExplainHandler extends AbstractRoutingHandler {
	private static final Logger LOGGER = Logger.getLogger(ReasonerExplainHandler.class.getName());

	@Inject
	public ReasonerExplainHandler(final ServerState theServerState,
	                              final Set<ServiceDecoder> theDecoders,
	                              final Set<ServiceEncoder> theEncoders) {
		super("POST", "{ontology}/explain", theServerState, theEncoders, theDecoders);
	}

	@Override
	public void handleRequest(final HttpServerExchange theExchange) throws Exception {
		final IRI ontology = getOntology(theExchange);
		final UUID clientId = getClientID(theExchange);

		int limit = getLimit(theExchange);

		byte[] inBytes = readInput(theExchange.getInputStream(), true);

		final ServiceDecoder decoderOpt = getDecoder(getContentType(theExchange));

		final ExplainRequest aExplainReq = decoderOpt.explainRequest(inBytes);

		final SchemaReasoner aReasoner = getReasoner(ontology, clientId);
		final Set<Set<OWLAxiom>> result = aReasoner.explain(aExplainReq.getAxiom(), limit);

		if (LOGGER.isLoggable(Level.INFO)) {
			StringWriter sw = new StringWriter();
			ManchesterSyntaxExplanationRenderer renderer = new ManchesterSyntaxExplanationRenderer();
			renderer.startRendering(sw);
			renderer.render(aExplainReq.getAxiom(), result);
			renderer.endRendering();
			LOGGER.info(sw.toString());
		}

		final ServiceEncoder encoderOpt = getEncoder(getAccept(theExchange));

		theExchange.getResponseSender()
		           .send(ByteBuffer.wrap(encoderOpt.encode(new ExplainResponse(result))));

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
