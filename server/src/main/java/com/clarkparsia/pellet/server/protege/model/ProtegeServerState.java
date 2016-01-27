package com.clarkparsia.pellet.server.protege.model;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.beust.jcommander.internal.Sets;
import com.clarkparsia.pellet.server.Configuration;
import com.clarkparsia.pellet.server.ConfigurationReader;
import com.clarkparsia.pellet.server.exceptions.ProtegeConnectionException;
import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.model.impl.OntologyStateImpl;
import com.clarkparsia.pellet.server.model.impl.ServerStateImpl;
import com.clarkparsia.pellet.server.protege.ProtegeServiceUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.RemoteOntologyDocument;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * @author Edgar Rodriguez-Diaz
 */
@Singleton
public final class ProtegeServerState extends ServerStateImpl {

	private static final Logger LOGGER = Logger.getLogger(ProtegeServerState.class.getName());

	private final Client mClient;

	private final IRI mServerRoot;

	private final Path mHome;

	/**
	 * Lock to control reloads of the state
	 */
	private final ReentrantLock updateLock = new ReentrantLock();

	@Inject
	public ProtegeServerState(final Configuration theConfig) throws ProtegeConnectionException, OWLOntologyCreationException {
		this(ConfigurationReader.of(theConfig));
	}

	ProtegeServerState(final ConfigurationReader theConfigReader) throws ProtegeConnectionException, OWLOntologyCreationException {
		super(ImmutableSet.<OntologyState>of());

		mHome = Paths.get(theConfigReader.pelletSettings().home());

		mClient = ProtegeServiceUtils.connect(theConfigReader);
		mServerRoot = IRI.create(mClient.getScheme() + "://" + mClient.getAuthority());

		for (String aOntology : theConfigReader.protegeSettings().ontologies()) {
			addOntology(aOntology);
		}
	}

	protected OntologyState createOntologyState(final String ontologyPath) throws OWLOntologyCreationException {
		final IRI serverRoot = IRI.create(mClient.getScheme() + "://" + mClient.getAuthority());
		LOGGER.info("Loading ontology " + ontologyPath);

		try {
			IRI remoteIRI = serverRoot.resolve("/"+ ontologyPath);
			RemoteOntologyDocument ontoDoc = (RemoteOntologyDocument) mClient.getServerDocument(remoteIRI);

			ProtegeOntologyState state = new ProtegeOntologyState(mClient, ontoDoc, mHome.resolve(ontologyPath).resolve("reasoner_state.bin"));

			LOGGER.info("Loaded revision " + state.getVersion());

			state.update();

			return state;
		}
		catch (Exception e) {
			throw new OWLOntologyCreationException("Could not load ontology from Protege server: " + ontologyPath, e);
		}
	}

	@Override
	public boolean update() {
		try {
			if (updateLock.tryLock(1, TimeUnit.SECONDS)) {
				return super.update();
			}
			else {
				LOGGER.info("Skipping update, there's another state update still happening");
			}
		}
		catch (InterruptedException ie) {
			LOGGER.log(Level.SEVERE, "Something interrupted a Server State update", ie);
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Could not refresh Server State from Protege", e);
		}
		finally {
			if (updateLock.isHeldByCurrentThread()) {
				updateLock.unlock();
			}
		}

		return false;
	}

	public Client getClient() {
		return mClient;
	}
}
