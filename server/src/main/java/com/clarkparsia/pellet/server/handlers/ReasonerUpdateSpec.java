package com.clarkparsia.pellet.server.handlers;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.clarkparsia.pellet.service.json.GenericJsonMessage;
import com.clarkparsia.pellet.service.json.JsonMessage;
import com.clarkparsia.pellet.service.messages.UpdateRequest;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.common.base.Optional;
import com.google.common.net.MediaType;
import com.google.inject.Inject;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.semanticweb.owlapi.model.IRI;

/**
 * Specification for {@link SchemaReasoner#update(Set, Set)} functionality within
 * the Pellet Server.
 *
 * @author Edgar Rodriguez-Diaz
 */
public class ReasonerUpdateSpec extends ReasonerSpec {

	@Inject
	public ReasonerUpdateSpec(final ServerState theServerState,
	                          final Set<ServiceDecoder> theDecoders,
	                          final Set<ServiceEncoder> theEncoders) {
		super(theServerState, theDecoders, theEncoders);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPath() {
		return path("{ontology}");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HttpHandler getHandler() {
		return wrapHandlerToMethod("PUT", new ReasonerUpdateHandler(mServerState, mEncoders, mDecoders));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PathType getPathType() {
		return PathType.TEMPLATE;
	}

	static class ReasonerUpdateHandler extends AbstractReasonerHandler {
		private static final Logger LOGGER = Logger.getLogger(ReasonerUpdateHandler.class.getName());

		public ReasonerUpdateHandler(final ServerState theServerState,
		                             final Collection<ServiceEncoder> theEncoders,
		                             final Collection<ServiceDecoder> theDecoders) {
			super(theServerState, theEncoders, theDecoders);
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

			final Optional<ServiceDecoder> decoderOpt = getDecoder(getContentType(theExchange));
			if (!decoderOpt.isPresent()) {
				// TODO: throw appropiate exception
				throw new ServerException(StatusCodes.NOT_ACCEPTABLE, "Couldn't decode request payload");
			}

			final UpdateRequest aUpdateRequest = decoderOpt.get().updateRequest(inBytes);

			final SchemaReasoner aReasoner = getReasoner(ontology, clientId);

			LOGGER.info("Updating client " + clientId +
			            " (+" + aUpdateRequest.getAdditions().size() + ", -" + aUpdateRequest.getAdditions().size() + ")");

			aReasoner.update(aUpdateRequest.getAdditions(), aUpdateRequest.getRemovals());

			LOGGER.info("Updating client " + clientId +
			            " Success!");

			theExchange.setStatusCode(StatusCodes.OK);

			if (MediaType.JSON_UTF_8.is(MediaType.parse(getAccept(theExchange)))) {
				final JsonMessage aJsonMessage = new GenericJsonMessage("Update successful.");

				theExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, JsonMessage.MIME_TYPE);
				theExchange.getResponseSender().send(aJsonMessage.toJsonString());
			}

			theExchange.endExchange();
		}
	}
}
