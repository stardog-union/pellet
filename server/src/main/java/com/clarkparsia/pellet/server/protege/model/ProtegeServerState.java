package com.clarkparsia.pellet.server.protege.model;

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
import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.model.impl.ServerStateImpl;
import com.clarkparsia.pellet.server.protege.ProtegeServiceUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.RemoteOntologyDocument;
import org.semanticweb.owlapi.model.IRI;

/**
 * @author Edgar Rodriguez-Diaz
 */
@Singleton
public final class ProtegeServerState extends ServerStateImpl {

	private static final Logger LOGGER = Logger.getLogger(ProtegeServerState.class.getName());

	private Client mClient;

	/**
	 * Lock to control reloads of the state
	 */
	private final ReentrantLock updateLock = new ReentrantLock();

	@Inject
	public ProtegeServerState(final Configuration theConfig) {
		this(ConfigurationReader.of(theConfig));
	}

	ProtegeServerState(final ConfigurationReader theConfigReader) {
		super(loadOntologies(theConfigReader));

		mClient = ProtegeServiceUtils.connect(theConfigReader);

		assert mClient != null;
	}

	/**
	 * Will load new allowed ontologies from the Protege Server.
	 *
	 * @return  the set of ontology states loaded from the server
	 */
	private static Set<OntologyState> loadOntologies(final ConfigurationReader configReader) {
		final Set<OntologyState> ontologies = Sets.newHashSet();
		final Set<String> allowedOntologies = ImmutableSet.copyOf(configReader.protegeSettings().ontologies());

		final Client mClient = ProtegeServiceUtils.connect(configReader);

		final IRI serverRoot = IRI.create(mClient.getScheme() + "://" + mClient.getAuthority());

		final Path home = Paths.get(configReader.pelletSettings().home());

		for (String aOntoName : allowedOntologies) {
			try {
				LOGGER.info("Loading ontology " + aOntoName);

				IRI remoteIRI = serverRoot.resolve("/"+ aOntoName);
				RemoteOntologyDocument ontoDoc = (RemoteOntologyDocument) mClient.getServerDocument(remoteIRI);

				ProtegeOntologyState state = new ProtegeOntologyState(mClient, ontoDoc, home.resolve(aOntoName).resolve("reasoner_state.bin"));

				LOGGER.info("Loaded revision " + state.getVersion());

				state.update();
				ontologies.add(state);
			}
			catch (Exception e) {
				LOGGER.log(Level.WARNING, "Could not load ontology from Protege server: " + aOntoName, e);
			}

		}

		return ontologies;
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

	@VisibleForTesting
	public void setClient(final Client theClient) {
		mClient = theClient;
	}
}
