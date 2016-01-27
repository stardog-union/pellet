package com.clarkparsia.pellet.server.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;

import com.clarkparsia.pellet.server.PelletServer;
import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.PathTemplateMatch;
import io.undertow.util.StatusCodes;
import org.semanticweb.owlapi.model.IRI;

/**
 * Abstract handler with tools for wrapping and setting up HttpHandlers implementing reasoner's functionality.
 *
 * @author Edgar Rodriguez-Diaz
 */
public abstract class AbstractRoutingHandler implements RoutingHandler {
	public static String REASONER_PATH = PelletServer.ROOT_PATH + "reasoner";

	private final String mPath;
	private final String mMethod;
	private final ServerState serverState;

	private final Collection<ServiceEncoder> mEncoders;

	private final Collection<ServiceDecoder> mDecoders;

	public AbstractRoutingHandler(final String theMethod,
	                              final String thePath,
	                              final ServerState theServerState,
	                              final Collection<ServiceEncoder> theEncoders,
	                              final Collection<ServiceDecoder> theDecoders) {
		serverState = theServerState;
		mEncoders = theEncoders;
		mDecoders = theDecoders;
		mMethod = theMethod;
		mPath = REASONER_PATH + "/" + thePath;
	}

	@Override
	public final String getMethod() {
		return mMethod;
	}

	@Override
	public final String getPath() {
		return mPath;
	}


	protected ServerState getServerState() {
		return serverState;
	}

	protected ServiceEncoder getEncoder(final String theMediaType) throws ServerException {
		for (ServiceEncoder encoder : mEncoders) {
			if (encoder.canEncode(theMediaType)) {
				return encoder;
			}
		}

		throw new ServerException(StatusCodes.NOT_ACCEPTABLE, "Could't decode request payload");
	}

	protected ServiceDecoder getDecoder(final String theMediaType) throws ServerException {
		for (ServiceDecoder decoder : mDecoders) {
			if (decoder.canDecode(theMediaType)) {
				return decoder;
			}
		}

		throw new ServerException(StatusCodes.NOT_ACCEPTABLE, "Could't decode request payload");
	}

	protected SchemaReasoner getReasoner(final IRI theOntology, final UUID theClientId) throws ServerException {
		return getOntologyState(theOntology).getClient(theClientId.toString())
		                                    .getReasoner();
	}

	protected OntologyState getOntologyState(final IRI theOntology) throws ServerException {
		Optional<OntologyState> aOntoState = getServerState().getOntology(theOntology);
		if (!aOntoState.isPresent()) {
			throw new ServerException(StatusCodes.NOT_FOUND, "Ontology not found: " + theOntology);
		}

		return aOntoState.get();
	}

	private String getHeaderValue(final HttpServerExchange theExchange,
	                              final HttpString theAttr,
	                              final String theDefault) {
		HeaderValues aVals = theExchange.getRequestHeaders().get(theAttr);

		return !aVals.isEmpty() ? aVals.getFirst()
		                        : theDefault;
	}

	/**
	 * TODO: Extend to handle multiple Accepts (the encoding/decoding too)
	 */
	protected String getAccept(final HttpServerExchange theExchange) {
		return getHeaderValue(theExchange,
		                      Headers.ACCEPT,
		                      mEncoders.iterator().next().getMediaType());
	}

	protected String getContentType(final HttpServerExchange theExchange) {
		return getHeaderValue(theExchange,
		                      Headers.CONTENT_TYPE,
		                      mDecoders.iterator().next().getMediaType());
	}

	protected void throwBadRequest(final String theMsg) throws ServerException {
		throw new ServerException(StatusCodes.BAD_REQUEST, theMsg);
	}

	protected IRI getOntology(final HttpServerExchange theExchange) throws ServerException {
		try {
			return IRI.create(URLDecoder.decode(theExchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY)
			                                               .getParameters().get("ontology"),
			                                    StandardCharsets.UTF_8.name()));
		}
		catch (Exception theE) {
			throw new ServerException(StatusCodes.BAD_REQUEST, "Error parsing Ontology IRI", theE);
		}
	}

	protected UUID getClientID(final HttpServerExchange theExchange) throws ServerException {
		try {
			return UUID.fromString(getQueryParameter(theExchange, "client"));
		}
		catch (IllegalArgumentException theE) {
			throw new ServerException(StatusCodes.BAD_REQUEST, "Error parsing Client ID - must be a UUID", theE);
		}
	}

	protected byte[] readInput(final InputStream theInStream,
	                           final boolean theFailOnEmpty /* Signal to fail on empty input */) throws ServerException {
		byte[] inBytes = {};
		try {
			try {
				inBytes = ByteStreams.toByteArray(theInStream);
			}
			finally {
				theInStream.close();
			}
		}
		catch (IOException theE) {
			throw new ServerException(500, "There was an IO error while reading input stream", theE);
		}

		if (theFailOnEmpty && inBytes.length == 0) {
			throw new ServerException(StatusCodes.NOT_ACCEPTABLE, "Payload is empty");
		}

		return inBytes;
	}

	protected String getQueryParameter(final HttpServerExchange theExchange,
	                                   final String theParamName) throws ServerException {
		final Map<String, Deque<String>> queryParams = theExchange.getQueryParameters();

		if (!queryParams.containsKey(theParamName) || queryParams.get(theParamName).isEmpty()) {
			throwBadRequest("Missing required query parameter: "+ theParamName);
		}

		final String paramVal = queryParams.get(theParamName).getFirst();
		if (Strings.isNullOrEmpty(paramVal)) {
			throwBadRequest("Query parameter ["+ theParamName +"] value is empty");
		}

		return paramVal;
	}
}
