package com.clarkparsia.pellet.server.protege.model;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.pellet.server.Configuration;
import com.clarkparsia.pellet.server.Environment;
import com.clarkparsia.pellet.server.exceptions.ProtegeConnectionException;
import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.server.model.impl.OntologyStateImpl;
import com.clarkparsia.pellet.server.model.impl.ServerStateImpl;
import com.clarkparsia.pellet.server.protege.ProtegeServiceUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
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

	private static String LOCAL_SENTINEL = "local";

	private static final Logger LOGGER = Logger.getLogger(ProtegeServerState.class.getName());

	private Client mClient;

	private final AtomicReference<ServerState> mServerState = new AtomicReference<ServerState>(ServerState.EMPTY);

	private final IRI serverRoot;

	private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

	private final boolean isStrict;

	/**
	 * Lock to control reloads of the state
	 */
	private ReentrantLock reloadLock = new ReentrantLock();

	@Inject
	public ProtegeServerState(final Configuration theConfig) {
		this(connectToProtege(theConfig), false);
	}

	@VisibleForTesting
	ProtegeServerState(final Client theProtegeClient,
	                   final boolean strictMode /* throws exception if can't load from disk with exception */) {
		mClient = theProtegeClient;
		serverRoot = IRI.create(mClient.getScheme() + "://" + mClient.getAuthority());

		isStrict = strictMode;

		// load any OntologyState elements saved on disk
		loadFromHome();
	}

	private void loadFromHome() {
		Path home = Paths.get(Environment.getHome());
		ImmutableSet.Builder<OntologyState> diskOntoStates = ImmutableSet.builder();
		File[] aFiles = home.toFile().listFiles();

		if (aFiles != null && aFiles.length > 0) {
			for (File aOntoDir : aFiles) {
				if (aOntoDir.isDirectory() && aOntoDir.canWrite() && aOntoDir.getName().endsWith(".history")) {
					try {
						LOGGER.info("Loading ontology from: "+ aOntoDir.getAbsolutePath());
						IRI docIRI = serverRoot.resolve("/"+ aOntoDir.getName());
						RemoteOntologyDocument ontoDoc = (RemoteOntologyDocument) getClient().getServerDocument(docIRI);
						diskOntoStates.add(ProtegeOntologyState.loadFromDisk(manager, mClient, aOntoDir, ontoDoc));
					}
					catch (Exception theE) {
						LOGGER.log(Level.SEVERE,
						           "Could not load OntologyState from disk on: " + aOntoDir.getAbsolutePath(),
						           theE);
						if (isStrict) {
							Throwables.propagate(theE);
						}
					}
				}
			}

			mServerState.set(ServerStateImpl.create(diskOntoStates.build()));
		}
	}

	/**
	 * Captures a snapshot of the state in Protege Server, loading new ontologies found or updating the ones
	 * already loaded.
	 *
	 * @return  the ServerState captured
	 */
	private ServerState snapshot() {
		ImmutableSet.Builder<OntologyState> newBuilder = ImmutableSet.builder();

		try {
			// scan the protege server to get all the ontologies.
			RemoteServerDocument rootDir = mClient.getServerDocument(serverRoot);
			Collection<RemoteOntologyDocument> docs = ProtegeServiceUtils.list(mClient, (RemoteServerDirectory) rootDir);

			for (RemoteOntologyDocument ontoDoc : docs) {

				try {
					final Optional<OntologyState> ontoState = findOntologyByServerLocation(ontoDoc.getServerLocation());
					if (ontoState.isPresent()) {
						LOGGER.info("Attempting to update ontology "+ ontoDoc.getServerLocation());
						ontoState.get().update();
					}
					else {
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

		final ImmutableSet<OntologyState> newOntologies = newBuilder.build();
		return newOntologies.isEmpty() ? mServerState.get()
		                               : ServerStateImpl.create(ImmutableSet.copyOf(Iterables.concat(newOntologies,
		                                                                                             this.ontologies())));
	}

	private Optional<OntologyState> findOntologyByServerLocation(final IRI theServerLocation) {
		return Optional.fromNullable(Iterables.find(ontologies(), new Predicate<OntologyState>() {
			@Override
			public boolean apply(final OntologyState input) {
				if (input instanceof ProtegeOntologyState) {
					return ((ProtegeOntologyState) input).getServerLocation()
					                                     .equals(theServerLocation);
				}
				return false;
			}
		}, null));
	}

	@Override
	public Optional<OntologyState> getOntology(final IRI ontology) {
		return mServerState.get().getOntology(ontology);
	}

	@Override
	public Iterable<OntologyState> ontologies() {
		return mServerState.get().ontologies();
	}

	@Override
	public boolean isEmpty() {
		return mServerState.get().isEmpty();
	}

	@Override
	public synchronized void update() {
		for (OntologyState aOntoState : ontologies()) {
			aOntoState.update();
		}
	}

	@Override
	public void save() {
		/**
		 * Update already goes through the ontologies and saves the changes to the incremental reasoner if there was any
		 * {@link OntologyStateImpl#update()}  }
 		 */
		update();
	}

	@Override
	public void reload() {
		// free resources from previous server state and update with new snapshot from server
		try {
			if (reloadLock.tryLock(1, TimeUnit.SECONDS)) {
				mServerState.getAndSet(snapshot())
				            .close();
			}
			else {
				LOGGER.info("Skipping reload, there's another state reload happening");
			}
		}
		catch (InterruptedException ie) {
			LOGGER.log(Level.SEVERE, "Something interrupted a Server State reload", ie);
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
		mServerState.get().close();
	}

	public static Client connectToProtege(final Configuration theConfiguration) {
		Properties aSettings = theConfiguration.getSettings();

		final String aHost = aSettings.getProperty(Configuration.PROTEGE_HOST);
		final int aPort = Integer.parseInt(aSettings.getProperty(Configuration.PROTEGE_PORT));
		final String aUser = aSettings.getProperty(Configuration.PROTEGE_USERNAME);
		final String aPassword = aSettings.getProperty(Configuration.PROTEGE_PASSWORD);

		Preconditions.checkArgument(aUser != null);
		Preconditions.checkArgument(aPassword != null);

		try {
			if (Strings.isNullOrEmpty(aHost) || LOCAL_SENTINEL.equals(aHost)) {
				// in case we might want to do embedded server with Protege Server
				throw new IllegalArgumentException("A host is required to connect to a Protege Server");
			}
			else {
				AuthToken authToken = RMILoginUtility.login("localhost", aPort, aUser, aPassword);
				RMIClient aClient = new RMIClient(authToken, "localhost", aPort);
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
