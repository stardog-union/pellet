package com.clarkparsia.pellet.server.protege.model;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.pellet.server.Environment;
import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.server.model.impl.OntologyStateImpl;
import com.clarkparsia.pellet.server.model.impl.ServerStateImpl;
import com.clarkparsia.pellet.server.protege.ProtegeServiceUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.RemoteOntologyDocument;
import org.protege.owl.server.api.client.RemoteServerDirectory;
import org.protege.owl.server.api.client.RemoteServerDocument;
import org.protege.owl.server.api.client.VersionedOntologyDocument;
import org.protege.owl.server.api.exception.OWLServerException;
import org.protege.owl.server.util.ClientUtilities;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * @author Edgar Rodriguez-Diaz
 */
public final class ProtegeServerState implements ServerState {

	private static final Logger LOGGER = Logger.getLogger(ProtegeServerState.class.getName());

	private final Client mClient;

	private final AtomicReference<ServerState> mServerState = new AtomicReference<ServerState>(ServerState.EMPTY);

	private final IRI serverRoot;

	private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

	private final boolean isStrict;

	public ProtegeServerState(final Client theProtegeClient) {
		this(theProtegeClient, false);
	}

	@VisibleForTesting
	ProtegeServerState(final Client theProtegeClient,
	                   final boolean strictMode /* throws exception if can't load from disk with exception */) {
		mClient = theProtegeClient;
		serverRoot = IRI.create(mClient.getScheme() + "://" + mClient.getAuthority());

		isStrict = strictMode;

		// load any OntologyState elements saved on disk
		loadFromHome();

		// this will update any ontology loaded from disk
		mServerState.set(snapshot());
	}

	private void loadFromHome() {
		Path home = Paths.get(Environment.getHome());
		ImmutableSet.Builder<OntologyState> diskOntoStates = ImmutableSet.builder();
		File[] aFiles = home.toFile().listFiles();

		if (aFiles != null && aFiles.length > 0) {
			for (File aOntoDir : aFiles) {
				if (aOntoDir.isDirectory() && aOntoDir.canWrite() && aOntoDir.getName().endsWith(".history")) {
					try {
						IRI docIRI = serverRoot.resolve("/"+ aOntoDir.getName());
						RemoteOntologyDocument ontoDoc = (RemoteOntologyDocument) getClient().getServerDocument(docIRI);
						diskOntoStates.add(ProtegeOntologyState.loadFromDisk(manager, mClient, aOntoDir, ontoDoc));
						LOGGER.info("Loaded ontology from: "+ aOntoDir.getAbsolutePath());
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
						LOGGER.info("Attempting to update OntologyState for "+ ontoDoc.getServerLocation());
						ontoState.get().update();
					}
					else {
						LOGGER.info("Creating new OntologyState for "+ ontoDoc.getServerLocation());
						VersionedOntologyDocument vont = ClientUtilities.loadOntology(mClient, manager, ontoDoc);
						newBuilder.add(new ProtegeOntologyState(mClient, vont));
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
	public void update() {
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
			mServerState.getAndSet(snapshot())
			            .close();
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Could not refresh Server State from Protege", e);
		}
	}

	public Client getClient() {
		return mClient;
	}

	@Override
	public void close() throws Exception {
		// close current server state
		mServerState.get().close();
	}
}
