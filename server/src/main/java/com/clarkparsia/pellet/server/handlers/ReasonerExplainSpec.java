package com.clarkparsia.pellet.server.handlers;

import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.Collection;
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
import io.undertow.server.HttpHandler;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PathType getPathType() {
		return PathType.TEMPLATE;
	}

	static class ReasonerExplainHandler extends AbstractReasonerHandler {
		private static final Logger LOGGER = Logger.getLogger(ReasonerExplainHandler.class.getName());

		public ReasonerExplainHandler(final ServerState theServerState,
		                              final Collection<ServiceEncoder> theEncoders,
		                              final Collection<ServiceDecoder> theDecoders) {
			super(theServerState, theEncoders, theDecoders);
		}

		@Override
		public void handleRequest(final HttpServerExchange theExchange) throws Exception {
			final IRI ontology = getOntology(theExchange);
			final UUID clientId = getClientID(theExchange);

			int limit = getLimit(theExchange);

			byte[] inBytes = readInput(theExchange.getInputStream(), true);

			final Optional<ServiceDecoder> decoderOpt = getDecoder(getContentType(theExchange));
			if (!decoderOpt.isPresent()) {
				// TODO: throw appropiate exception
				throw new ServerException(StatusCodes.NOT_ACCEPTABLE, "Cannot decode request payload");
			}

			final ExplainRequest aExplainReq = decoderOpt.get().explainRequest(inBytes);

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

			final Optional<ServiceEncoder> encoderOpt = getEncoder(getAccept(theExchange));
			if (!encoderOpt.isPresent()) {
				// TODO: throw appropiate exception
				throw new ServerException(StatusCodes.NOT_ACCEPTABLE, "Cannot encode response payload");
			}

			theExchange.getResponseSender()
			           .send(ByteBuffer.wrap(encoderOpt.get()
			                                           .encode(new ExplainResponse(result))));

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
}
