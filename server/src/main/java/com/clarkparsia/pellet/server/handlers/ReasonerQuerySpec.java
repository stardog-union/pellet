package com.clarkparsia.pellet.server.handlers;

import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.Set;

import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.clarkparsia.pellet.service.messages.QueryRequest;
import com.clarkparsia.pellet.service.messages.QueryResponse;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import io.undertow.Handlers;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.ExceptionHandler;
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
		ExceptionHandler aExceptionHandler = new ExceptionHandler(new ReasonerQueryHandler(mServerState, mEncoders, mDecoders));
		aExceptionHandler.addExceptionHandler(ServerException.class, new PelletExceptionHandler());

		BlockingHandler aFnHandler = new BlockingHandler(aExceptionHandler);

		return Handlers.predicate(Predicates.parse("method(POST)"),
		                          aFnHandler,   // true
		                          new MethodNotAllowedHandler("POST")); //false
	}

	@Override
	public PathType getPathType() {
		return PathType.TEMPLATE;
	}

	static class ReasonerQueryHandler extends AbstractHttpHandler {


		public ReasonerQueryHandler(final ServerState theServerState,
		                            final Collection<ServiceEncoder> theEncoders,
		                            final Collection<ServiceDecoder> theDecoders) {
			super(theServerState, theEncoders, theDecoders);
		}

		@Override
		public void handleRequest(final HttpServerExchange theExchange) throws Exception {
			final String ontology = URLDecoder.decode(theExchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY)
			                                                     .getParameters().get("ontology"),
			                                          StandardCharsets.UTF_8.name());


			final SchemaReasoner.QueryType queryType = getQueryType(theExchange);

			InputStream inStream = theExchange.getInputStream();
			byte[] inBytes = {};
			try {
				inBytes = ByteStreams.toByteArray(inStream);
			}
			finally {
				inStream.close();
			}

			if (inBytes.length == 0) {
				throw new ServerException(StatusCodes.NOT_ACCEPTABLE, "Payload is empty");
			}

			final Optional<ServiceDecoder> decoderOpt = getDecoder(getContentType(theExchange));
			if (!decoderOpt.isPresent()) {
				// TODO: throw appropiate exception
				throw new ServerException(StatusCodes.NOT_ACCEPTABLE, "Could't decode request payload");
			}

			final QueryRequest aQueryReq = decoderOpt.get().queryRequest(inBytes);

			// TODO: Is this the best way to identify the client?
			final String clientId = theExchange.getSourceAddress().toString();

			final SchemaReasoner aReasoner = getReasoner(IRI.create(ontology), clientId);
			final NodeSet<? extends OWLObject> result = aReasoner.query(queryType, aQueryReq.getInput());

			final Optional<ServiceEncoder> encoderOpt = getEncoder(getAccept(theExchange));
			if (!encoderOpt.isPresent()) {
				// TODO: throw appropiate exception
				throw new ServerException(StatusCodes.NOT_ACCEPTABLE, "Could't encode response payload");
			}

			theExchange.getResponseSender()
			                     .send(ByteBuffer.wrap(encoderOpt.get()
			                                                     .encode(new QueryResponse(result))));

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

		private void throwBadRequest(final String theMsg) throws ServerException {
			throw new ServerException(StatusCodes.BAD_REQUEST, theMsg);
		}
	}
}
