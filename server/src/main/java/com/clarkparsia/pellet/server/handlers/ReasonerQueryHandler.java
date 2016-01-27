package com.clarkparsia.pellet.server.handlers;

import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.clarkparsia.pellet.service.messages.QueryRequest;
import com.clarkparsia.pellet.service.messages.QueryResponse;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * Specification for {@link SchemaReasoner#query(SchemaReasoner.QueryType, OWLLogicalEntity)} functionality within
 * the Pellet Server.
 *
 * @author Edgar Rodriguez-Diaz
 */
public class ReasonerQueryHandler extends AbstractRoutingHandler {

	@Inject
	public ReasonerQueryHandler(final ServerState theServerState,
	                            final Set<ServiceDecoder> theDecoders,
	                            final Set<ServiceEncoder> theEncoders) {
		super("POST", "{ontology}/query", theServerState, theEncoders, theDecoders);
	}

	@Override
	public void handleRequest(final HttpServerExchange theExchange) throws Exception {
		final IRI ontology = getOntology(theExchange);
		final UUID clientId = getClientID(theExchange);
		final SchemaReasoner.QueryType queryType = getQueryType(theExchange);

		byte[] inBytes = readInput(theExchange.getInputStream(), true);

		final ServiceDecoder decoderOpt = getDecoder(getContentType(theExchange));

		final QueryRequest aQueryReq = decoderOpt.queryRequest(inBytes);

		final SchemaReasoner aReasoner = getReasoner(ontology, clientId);
		final NodeSet<? extends OWLObject> result = aReasoner.query(queryType, aQueryReq.getInput());

		final ServiceEncoder encoderOpt = getEncoder(getAccept(theExchange));

		theExchange.getResponseSender()
		                     .send(ByteBuffer.wrap(encoderOpt.encode(new QueryResponse(result))));

		theExchange.endExchange();
	}

	private SchemaReasoner.QueryType getQueryType(final HttpServerExchange theExchange) throws ServerException {
		final Map<String, Deque<String>> queryParams = theExchange.getQueryParameters();

		if (!queryParams.containsKey("type") || queryParams.get("type").isEmpty()) {
			throwBadRequest("Missing required query parameter: type");
		}

		final String queryTypeStr = queryParams.get("type").getFirst();

		if (Strings.isNullOrEmpty(queryTypeStr)) {
			throwBadRequest("Missing required query parameter: type");
		}

		SchemaReasoner.QueryType queryType = null;
		try {
			queryType = SchemaReasoner.QueryType.valueOf(queryTypeStr);
		}
		catch (Exception e) {
			throwBadRequest("Query 'type' parameter is not valid.");
		}

		return queryType;
	}
}
