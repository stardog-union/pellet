package com.clarkparsia.pellet.server.protege.model;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.beust.jcommander.internal.Sets;
import com.clarkparsia.pellet.server.Configuration;
import com.clarkparsia.pellet.server.ConfigurationReader;
import com.clarkparsia.pellet.server.Environment;
import com.clarkparsia.pellet.server.exceptions.ProtegeConnectionException;
import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.server.model.impl.ServerStateImpl;
import com.clarkparsia.pellet.server.protege.ProtegeServiceUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.protege.owl.server.api.AuthToken;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.RemoteOntologyDocument;
import org.protege.owl.server.api.client.RemoteServerDirectory;
import org.protege.owl.server.api.client.RemoteServerDocument;
import org.protege.owl.server.api.client.VersionedOntologyDocument;
import org.protege.owl.server.api.exception.OWLServerException;
import org.protege.owl.server.connect.rmi.RMIClient;
import org.protege.owl.server.policy.RMILoginUtility;
import org.protege.owl.server.util.ClientUtilities;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * @author Edgar Rodriguez-Diaz
 */
@Singleton
public final class ProtegeServerState implements ServerState {

	private static final Logger LOGGER = Logger.getLogger(ProtegeServerState.class.getName());

	private Client mClient;

	private final ServerState serverState;

	private final IRI serverRoot;

	private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

	/**
	 * Lock to control reloads of the state
	 */
	private final ReentrantLock reloadLock = new ReentrantLock();

	private final ConfigurationReader configReader;

	@Inject
	public ProtegeServerState(final Configuration theConfig) {
		this(ConfigurationReader.of(theConfig));
	}

	ProtegeServerState(final ConfigurationReader theConfigReader) {
		mClient = connectToProtege(theConfigReader);

		assert mClient != null;

		serverRoot = IRI.create(mClient.getScheme() + "://" + mClient.getAuthority());

		configReader = theConfigReader;

		serverState = loadState();
	}

	/**
	 * Will load ontology states from disk
	 *
	 * @return  the ontology states loaded from disk
	 */
	private Set<OntologyState> loadFromHome(final Set<String> theAlreadyLoaded) {
		final Path home = Paths.get(Environment.getHome());
		final ImmutableSet.Builder<OntologyState> diskOntoStates = ImmutableSet.builder();
		final File[] aFiles = home.toFile().listFiles();

		if (aFiles != null && aFiles.length > 0) {
			for (File aOntoDir : aFiles) {
				if (aOntoDir.isDirectory() && aOntoDir.canWrite() && aOntoDir.getName().endsWith(".history")) {
					try {
						LOGGER.info("Loading ontology from: "+ aOntoDir.getAbsolutePath());
						IRI docIRI = serverRoot.resolve("/"+ aOntoDir.getName());
						RemoteOntologyDocument ontoDoc = (RemoteOntologyDocument) getClient().getServerDocument(docIRI);
						diskOntoStates.add(ProtegeOntologyState.loadFromDisk(manager, mClient, aOntoDir, ontoDoc));

						theAlreadyLoaded.add(docIRI.getFragment());
					}
					catch (Exception theE) {
						LOGGER.log(Level.SEVERE,
						           "Could not load OntologyState from disk on: " + aOntoDir.getAbsolutePath(),
						           theE);
						if (configReader.pelletSettings().isStrict()) {
							Throwables.propagate(theE);
						}
					}
				}
			}
		}
		return diskOntoStates.build();
	}

	/**
	 * Will load new allowed ontologies from the Protege Server.
	 *
	 * @return  the set of ontology states loaded from the server
	 */
	private Set<OntologyState> loadFromServer(final Set<String> theAlreadyLoaded) {
		final ImmutableSet.Builder<OntologyState> newBuilder = ImmutableSet.builder();
		final Set<String> allowedOntologies = ImmutableSet.copyOf(configReader.protegeSettings().ontologies());

		try {
			// scan the protege server to get all the ontologies.
			RemoteServerDocument rootDir = mClient.getServerDocument(serverRoot);
			Collection<RemoteOntologyDocument> docs = ProtegeServiceUtils.list(mClient, (RemoteServerDirectory) rootDir);

			for (RemoteOntologyDocument ontoDoc : docs) {
				try {
					if (!theAlreadyLoaded.contains(ontoDoc.getServerLocation().getFragment()) &&
					    (allowedOntologies.isEmpty() || allowedOntologies.contains(ontoDoc.getServerLocation().getFragment()))) {
						LOGGER.info("Found new ontology "+ ontoDoc.getServerLocation());
						VersionedOntologyDocument vont = ClientUtilities.loadOntology(mClient, manager, ontoDoc);
						ProtegeOntologyState state = new ProtegeOntologyState(mClient, vont);
						newBuilder.add(state);
						state.save();
					}
				}
				catch (OWLOntologyCreationException e) {
					LOGGER.log(Level.FINER, "Could not load one or more ontologies from Protege server", e);
				}
			}
		}
		catch (OWLServerException e) {
			LOGGER.log(Level.FINER, "Could not capture snapshot of ontologies from Protege server", e);
			Throwables.propagate(e);
		}

		return newBuilder.build();
	}

	private ServerState loadState() {
		final ImmutableSet.Builder<OntologyState> allOntologies = ImmutableSet.builder();

		// Will collect the ontologies we're loading
		final Set<String> loadedOntologies = Sets.newHashSet();

		// merge found and new allowed ontologies
		allOntologies.addAll(loadFromHome(loadedOntologies));
		allOntologies.addAll(loadFromServer(loadedOntologies));

		return ServerStateImpl.create(allOntologies.build());
	}

	@Override
	public Optional<OntologyState> getOntology(final IRI ontology) {
		return serverState.getOntology(ontology);
	}

	@Override
	public Iterable<OntologyState> ontologies() {
		return serverState.ontologies();
	}

	@Override
	public boolean isEmpty() {
		return serverState.isEmpty();
	}

	@Override
	public void update() {
		// free resources from previous server state and update with new snapshot from server
		try {
			if (reloadLock.tryLock(1, TimeUnit.SECONDS)) {
				serverState.update();
			}
			else {
				LOGGER.info("Skipping reload, there's another state reload happening");
			}
		}
		catch (InterruptedException ie) {
			LOGGER.log(Level.SEVERE, "Something interrupted a Server State update", ie);
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Could not refresh Server State from Protege", e);
		}
		finally {
			if (reloadLock.isHeldByCurrentThread()) {
				reloadLock.unlock();
			}
		}
	}

	@Override
	public void save() {
		serverState.save();
	}

	@Override
	public void reload() {
		update();
	}

	public Client getClient() {
		return mClient;
	}

	@VisibleForTesting
	public void setClient(final Client theClient) {
		mClient = theClient;
	}

	@Override
	public void close() throws Exception {
		// close current server state
		serverState.close();
	}

	public static Client connectToProtege(final ConfigurationReader config) {
		final ConfigurationReader.ProtegeSettings protege = config.protegeSettings();
		final String aHost = protege.host();

		try {
			if (Strings.isNullOrEmpty(aHost) || "local".equals(aHost)) {
				// in case we might want to do embedded server with Protege Server
				throw new IllegalArgumentException("A host is required to connect to a Protege Server");
			}
			else {
				AuthToken authToken = RMILoginUtility.login(aHost,
				                                            protege.port(),
				                                            protege.username(),
				                                            protege.password());
				RMIClient aClient = new RMIClient(authToken, aHost, protege.port());
				aClient.initialise();

				return aClient;
			}
		}
		catch (Exception e) {
			Throwables.propagate(new ProtegeConnectionException("Could not connect to Protege Server", e));
		}
		return null;
	}
}
