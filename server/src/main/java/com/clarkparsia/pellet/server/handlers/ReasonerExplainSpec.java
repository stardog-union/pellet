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
import com.clarkparsia.pellet.service.messages.ExplainRequest;
import com.clarkparsia.pellet.service.messages.ExplainResponse;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplateMatch;
import io.undertow.util.StatusCodes;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ReasonerExplainSpec extends ReasonerSpec {

	@Inject
	public ReasonerExplainSpec(final ServerState theServerState,
	                           final Set<ServiceDecoder> theDecoders,
	                           final Set<ServiceEncoder> theEncoders) {
		super(theServerState, theDecoders, theEncoders);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPath() {
		return path("{ontology}/explain");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HttpHandler getHandler() {
		return wrapHandlerToMethod("POST", new ReasonerExplainHandler(mServerState, mEncoders, mDecoders));
	}

	@Override
	public PathType getPathType() {
		return PathType.TEMPLATE;
	}

	static class ReasonerExplainHandler extends AbstractHttpHandler {

		public ReasonerExplainHandler(final ServerState theServerState,
		                              final Collection<ServiceEncoder> theEncoders,
		                              final Collection<ServiceDecoder> theDecoders) {
			super(theServerState, theEncoders, theDecoders);
		}

		@Override
		public void handleRequest(final HttpServerExchange theExchange) throws Exception {
			final String ontology = URLDecoder.decode(theExchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY)
			                                                     .getParameters().get("ontology"),
			                                          StandardCharsets.UTF_8.name());

			int limit = getLimit(theExchange);

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

			final ExplainRequest aExplainReq = decoderOpt.get().explainRequest(inBytes);

			// TODO: Is this the best way to identify the client?
			final String clientId = theExchange.getSourceAddress().toString();

			final SchemaReasoner aReasoner = getReasoner(IRI.create(ontology), clientId);
			final Set<Set<OWLAxiom>> result = aReasoner.explain(aExplainReq.getAxiom(), limit);

			final Optional<ServiceEncoder> encoderOpt = getEncoder(getAccept(theExchange));
			if (!encoderOpt.isPresent()) {
				// TODO: throw appropiate exception
				throw new ServerException(StatusCodes.NOT_ACCEPTABLE, "Could't encode response payload");
			}

			theExchange.getResponseSender()
			           .send(ByteBuffer.wrap(encoderOpt.get()
			                                           .encode(new ExplainResponse(result))));

			theExchange.endExchange();
		}

		private int getLimit(final HttpServerExchange theExchange) throws ServerException {
			final Map<String, Deque<String>> queryParams = theExchange.getQueryParameters();
			int limit = 0;

			if (!queryParams.containsKey("limit") || queryParams.get("limit").isEmpty()) {
				throwBadRequest("Missing required query parameter: limit");
			}

			final String limitStr = queryParams.get("limit").getFirst();

			if (Strings.isNullOrEmpty(limitStr)) {
				throwBadRequest("Missing required query parameter: limit");
			}

			try {
				limit = Integer.parseInt(limitStr);
			}
			catch (Exception e) {
				throwBadRequest("Query 'limit' parameter is not valid, it must be an Integer.");
			}

			return limit;
		}


	}
}
