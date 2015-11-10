package com.clarkparsia.pellet.server.handlers;

import java.util.Collection;
import java.util.Set;

import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ReasonerVersionSpec extends ReasonerSpec {

	@Inject
	public ReasonerVersionSpec(final ServerState theServerState,
	                           final Set<ServiceDecoder> theDecoders,
	                           final Set<ServiceEncoder> theEncoders) {
		super(theServerState, theDecoders, theEncoders);
	}

	@Override
	public String getPath() {
		return path("{ontology}/version");
	}

	@Override
	public HttpHandler getHandler() {
		return wrapHandlerToMethod("GET", new ReasonerVersionHandler(mServerState, mEncoders, mDecoders));
	}

	@Override
	public PathType getPathType() {
		return PathType.TEMPLATE;
	}

	static class ReasonerVersionHandler extends AbstractReasonerHandler {

		public ReasonerVersionHandler(final ServerState theServerState,
		                              final Collection<ServiceEncoder> theEncoders,
		                              final Collection<ServiceDecoder> theDecoders) {
			super(theServerState, theEncoders, theDecoders);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void handleRequest(final HttpServerExchange theExchange) throws Exception {
			// TODO: Is this the best way to identify the client?
			final String clientId = theExchange.getSourceAddress().toString();

			// Get local client reasoner's version
			int version = getReasoner(getOntology(theExchange), clientId).version();

			JsonObject aJsonResp = new JsonObject();
			aJsonResp.addProperty("version", version);

			theExchange.setStatusCode(StatusCodes.OK);
			theExchange.getResponseSender().send(aJsonResp.toString());
			theExchange.endExchange();
		}
	}
}
