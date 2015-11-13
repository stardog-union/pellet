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
import com.clarkparsia.pellet.server.Environment;
import com.clarkparsia.pellet.server.exceptions.ProtegeConnectionException;
import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.model.impl.ServerStateImpl;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.protege.owl.server.api.AuthToken;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.RemoteOntologyDocument;
import org.protege.owl.server.connect.rmi.RMIClient;
import org.protege.owl.server.policy.RMILoginUtility;
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
	private final ReentrantLock reloadLock = new ReentrantLock();

	private final ConfigurationReader configReader;

	@Inject
	public ProtegeServerState(final Configuration theConfig) {
		this(ConfigurationReader.of(theConfig));
	}

	ProtegeServerState(final ConfigurationReader theConfigReader) {
		super(loadOntologies(theConfigReader));

		mClient = connectToProtege(theConfigReader);

		assert mClient != null;

		configReader = theConfigReader;
	}

	/**
	 * Will load new allowed ontologies from the Protege Server.
	 *
	 * @return  the set of ontology states loaded from the server
	 */
	private static Set<OntologyState> loadOntologies(final ConfigurationReader configReader) {
		final Set<OntologyState> ontologies = Sets.newHashSet();
		final Set<String> allowedOntologies = ImmutableSet.copyOf(configReader.protegeSettings().ontologies());

		final Client mClient = connectToProtege(configReader);

		final IRI serverRoot = IRI.create(mClient.getScheme() + "://" + mClient.getAuthority());

		final Path home = Paths.get(Environment.getHome());

		for (String aOntoName : allowedOntologies) {
			try {
				LOGGER.info("Loading ontology " + aOntoName);

				IRI remoteIRI = serverRoot.resolve("/"+ aOntoName);
				RemoteOntologyDocument ontoDoc = (RemoteOntologyDocument) mClient.getServerDocument(remoteIRI);

				ProtegeOntologyState state = new ProtegeOntologyState(mClient, ontoDoc, home.resolve(aOntoName).resolve("reasoner_state.bin"));

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
	public void update() {
		// free resources from previous server state and update with new snapshot from server
		try {
			if (reloadLock.tryLock(1, TimeUnit.SECONDS)) {
				super.update();
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

	public Client getClient() {
		return mClient;
	}

	@VisibleForTesting
	public void setClient(final Client theClient) {
		mClient = theClient;
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
