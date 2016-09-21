package com.clarkparsia.pellet.server.protege.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.pellet.server.Configuration;
import com.clarkparsia.pellet.server.ConfigurationReader;
import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.model.impl.ServerStateImpl;
import com.clarkparsia.pellet.server.protege.ProtegeServiceUtils;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.stanford.protege.metaproject.api.ProjectId;
import edu.stanford.protege.metaproject.impl.ProjectIdImpl;
import org.protege.editor.owl.client.LocalHttpClient;
import org.protege.editor.owl.client.api.exception.ClientRequestException;
import org.protege.editor.owl.server.versioning.api.ServerDocument;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * @author Edgar Rodriguez-Diaz
 */
@Singleton
public final class ProtegeServerState extends ServerStateImpl {

	private static final Logger LOGGER = Logger.getLogger(ProtegeServerState.class.getName());

	private final LocalHttpClient mClient;

	private final Path mHome;

	/**
	 * Lock to control reloads of the state
	 */
	private final ReentrantLock updateLock = new ReentrantLock();

	@Inject
	public ProtegeServerState(final Configuration theConfig) throws Exception {
		this(ConfigurationReader.of(theConfig));
	}

	ProtegeServerState(final ConfigurationReader theConfigReader) throws Exception {
		super(ImmutableSet.<OntologyState>of());

		mHome = Paths.get(theConfigReader.pelletSettings().home());

		mClient = ProtegeServiceUtils.connect(theConfigReader);

		Set<String> onts = theConfigReader.protegeSettings().ontologies();
		for (String ont : onts) {
			addOntology(ont);
		}
	}

	@Override
	protected OntologyState createOntologyState(final String ontologyPath) throws OWLOntologyCreationException {
		LOGGER.info("Loading ontology " + ontologyPath);

		try {
			ProjectId projectID = new ProjectIdImpl(ontologyPath);

			ProtegeOntologyState state = new ProtegeOntologyState(mClient, projectID, mHome.resolve(projectID.get()).resolve("reasoner_state.bin"));

			LOGGER.info("Loaded revision " + state.getVersion());

			state.update();

			return state;
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
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

	public LocalHttpClient getClient() {
		return mClient;
	}
}
