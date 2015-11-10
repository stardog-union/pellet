package com.clarkparsia.pellet.server.handlers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;

import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.clarkparsia.pellet.service.json.GenericJsonMessage;
import com.clarkparsia.pellet.service.json.JsonMessage;
import com.clarkparsia.pellet.service.messages.UpdateRequest;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.PathTemplateMatch;
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

		public ReasonerUpdateHandler(final ServerState theServerState,
		                             final Collection<ServiceEncoder> theEncoders,
		                             final Collection<ServiceDecoder> theDecoders) {
			super(theServerState, theEncoders, theDecoders);
		}

		@Override
		public void handleRequest(final HttpServerExchange theExchange) throws Exception {
			final String ontology = URLDecoder.decode(theExchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY)
			                                                     .getParameters().get("ontology"),
			                                          StandardCharsets.UTF_8.name());

			byte[] inBytes = readInput(theExchange.getInputStream(), false /* don't fail on empty input */);

			if (inBytes.length == 0) {
				// If there's no payload we finish the exchange
				theExchange.setStatusCode(StatusCodes.NOT_MODIFIED);
				theExchange.endExchange();
				return;
			}

			final Optional<ServiceDecoder> decoderOpt = getDecoder(getContentType(theExchange));
			if (!decoderOpt.isPresent()) {
				// TODO: throw appropiate exception
				throw new ServerException(StatusCodes.NOT_ACCEPTABLE, "Could't decode request payload");
			}

			final UpdateRequest aUpdateRequest = decoderOpt.get().updateRequest(inBytes);

			// TODO: Is this the best way to identify the client?
			final String clientId = theExchange.getSourceAddress().toString();

			final SchemaReasoner aReasoner = getReasoner(IRI.create(ontology), clientId);
			aReasoner.update(aUpdateRequest.getAdditions(), aUpdateRequest.getRemovals());

			final Optional<ServiceEncoder> encoderOpt = getEncoder(getAccept(theExchange));
			if (!encoderOpt.isPresent()) {
				// TODO: throw appropiate exception
				throw new ServerException(StatusCodes.NOT_ACCEPTABLE, "Could't encode response payload");
			}

			JsonMessage aJsonMessage = new GenericJsonMessage("Update successful.");

			theExchange.setStatusCode(StatusCodes.OK);
			theExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, JsonMessage.MIME_TYPE);
			theExchange.getResponseSender().send(aJsonMessage.toJsonString());
			theExchange.endExchange();
		}
	}
}
