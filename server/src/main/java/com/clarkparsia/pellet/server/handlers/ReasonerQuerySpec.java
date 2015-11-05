package com.clarkparsia.pellet.server.handlers;

import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.model.ClientState;
import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.clarkparsia.pellet.service.json.GenericJsonMessage;
import com.clarkparsia.pellet.service.messages.QueryRequest;
import com.clarkparsia.pellet.service.messages.QueryResponse;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import io.undertow.Handlers;
import io.undertow.predicate.Predicates;
import io.undertow.server.BlockingHttpExchange;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.util.Headers;
import io.undertow.util.PathTemplateMatch;
import io.undertow.util.StatusCodes;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ReasonerQuerySpec extends ReasonerSpec {

	private final Set<ServiceDecoder> mDecoders;
	private final Set<ServiceEncoder> mEncoders;

	@Inject
	public ReasonerQuerySpec(final ServerState theServerState,
	                         final Set<ServiceDecoder> theDecoders,
	                         final Set<ServiceEncoder> theEncoders) {
		super(theServerState);

		mDecoders = theDecoders;
		mEncoders = theEncoders;
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

	class ReasonerQueryHandler implements HttpHandler {

		private final ServerState serverState;

		public ReasonerQueryHandler(final ServerState theServerState) {
			serverState = theServerState;
		}

		@Override
		public void handleRequest(final HttpServerExchange theHttpServerExchange) throws Exception {
			if (theHttpServerExchange.isInIoThread()) {
				theHttpServerExchange.dispatch(this);
				return;
			}

			String ontology = URLDecoder.decode(theHttpServerExchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY)
			                                                         .getParameters().get("ontology"),
			                                    StandardCharsets.UTF_8.name());

			String queryTypeStr = theHttpServerExchange.getQueryParameters().get("type").getFirst();

			if (Strings.isNullOrEmpty(queryTypeStr)) {
				// TODO: send exception that needs "type" query parameter.
				throw new ServerException(400, "Missing required query parameter: type");
			}

			SchemaReasoner.QueryType queryType = SchemaReasoner.QueryType.valueOf(queryTypeStr);

			theHttpServerExchange.startBlocking();
			byte[] aBytes = ByteStreams.toByteArray(theHttpServerExchange.getInputStream());

			String aContentType = theHttpServerExchange.getRequestHeaders().get(Headers.CONTENT_TYPE).getFirst();

			Optional<ServiceDecoder> decoderOpt = getDecoder(aContentType);
			if (!decoderOpt.isPresent()) {
				// TODO: throw appropiate exception
				throw new ServerException(400, "Could't decode request payload");
			}

			Optional<OntologyState> ontoOpt = serverState.getOntology(IRI.create(ontology));

			if (ontoOpt.isPresent()) {
				final QueryRequest aQueryReq = decoderOpt.get().queryRequest(aBytes);

				final OntologyState ontoState = ontoOpt.get();
				final String clientId = theHttpServerExchange.getSourceAddress().toString();

				final ClientState clientState = ontoState.getClient(clientId);
				SchemaReasoner aReasoner = clientState.getReasoner();

				NodeSet<? extends OWLObject> result = aReasoner.query(queryType, aQueryReq.getInput());

				String aAccept = theHttpServerExchange.getRequestHeaders().get(Headers.ACCEPT).getFirst();
				Optional<ServiceEncoder> encoderOpt = getEncoder(aAccept);
				if (!encoderOpt.isPresent()) {
					// TODO: throw appropiate exception
					throw new ServerException(400, "Could't encode response payload");
				}

				theHttpServerExchange.getResponseSender()
				                     .send(ByteBuffer.wrap(encoderOpt.get()
				                                                     .encode(new QueryResponse(result))));
				theHttpServerExchange.endExchange();
			}
			else {
				// Just send 404 when the ontology is not found.
				theHttpServerExchange.setStatusCode(StatusCodes.NOT_FOUND);
				theHttpServerExchange.getResponseSender().send("Ontology not found.");
				theHttpServerExchange.endExchange();
			}
		}

		private Optional<ServiceEncoder> getEncoder(final String theMediaType) {
			Optional<ServiceEncoder> aFound = Optional.absent();

			for (ServiceEncoder encoder : ReasonerQuerySpec.this.mEncoders) {
				if (encoder.canEncode(theMediaType)) {
					aFound = Optional.of(encoder);
				}
			}

			return aFound;
		}

		private Optional<ServiceDecoder> getDecoder(final String theMediaType) {
			Optional<ServiceDecoder> aFound = Optional.absent();

			for (ServiceDecoder decoder : ReasonerQuerySpec.this.mDecoders) {
				if (decoder.canDecode(theMediaType)) {
					aFound = Optional.of(decoder);
				}
			}

			return aFound;
		}
	}
}
