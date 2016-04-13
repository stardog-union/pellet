package com.clarkparsia.pellet.server.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellet.server.PelletServer;
import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.model.ClientState;
import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.PathTemplateMatch;
import io.undertow.util.StatusCodes;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

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

	protected final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

	public AbstractRoutingHandler(final String theMethod,
	                              final String thePath,
	                              final ServerState theServerState) {
		serverState = theServerState;
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

	protected SchemaReasoner getReasoner(final IRI theOntology, final UUID theClientId) throws ServerException {
		return getClientState(theOntology, theClientId).getReasoner();
	}

	protected ClientState getClientState(final IRI theOntology, final UUID theClientId) throws ServerException {
		return getOntologyState(theOntology).getClient(theClientId);
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
		                      null);
	}

	protected String getContentType(final HttpServerExchange theExchange) {
		return getHeaderValue(theExchange,
		                      Headers.CONTENT_TYPE,
		                      null);
	}

	protected ServerException throwBadRequest(final String theMsg) throws ServerException {
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

	protected String getQueryParameter(final HttpServerExchange theExchange,
	                                   final String paramName) throws ServerException {
		final Map<String, Deque<String>> queryParams = theExchange.getQueryParameters();

		if (!queryParams.containsKey(paramName) || queryParams.get(paramName).isEmpty()) {
			throwBadRequest("Missing required parameter: "+ paramName);
		}

		final String paramVal = queryParams.get(paramName).getFirst();
		if (Strings.isNullOrEmpty(paramVal)) {
			throwBadRequest("Missing required parameter: " + paramName);
		}

		return paramVal;
	}


	protected <E extends Enum<E>> E getQueryParameter(final HttpServerExchange theExchange, String paramName, Class<E> enumType) throws ServerException {
		final String paramVal =getQueryParameter(theExchange, paramName);

		try {
			return Enum.valueOf(enumType, paramVal);
		}
		catch (Exception e) {
			throw throwBadRequest("Invalid parameter value: " + paramVal);
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

	protected Set<OWLAxiom> readAxioms(final InputStream theInStream) throws ServerException {
		OWLOntology ontology = null;
		try {
			ontology = manager.loadOntologyFromOntologyDocument(theInStream);

			return ontology.getAxioms();
		}
		catch (OWLOntologyCreationException e) {
			throw new ServerException(400, "There was an error parsing axioms", e);
		}
		catch (Exception e) {
			throw new ServerException(500, "There was an IO error while reading input stream", e);
		}
		finally {
			try {
				theInStream.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			if (ontology != null) {
				OWL.manager.removeOntology(ontology);
			}
		}
	}

	protected OWLAxiom readAxiom(final InputStream theInStream) throws ServerException {
		Set<OWLAxiom> axioms = readAxioms(theInStream);
		Iterables.removeIf(axioms, new Predicate<OWLAxiom>() {
			@Override
			public boolean apply(final OWLAxiom axiom) {
				return !axiom.isLogicalAxiom();
			}
		});
		if (axioms.size() != 1) {
			throw new ServerException(422, "Input should contain a single logical axiom");
		}
		return axioms.iterator().next();
	}
}
