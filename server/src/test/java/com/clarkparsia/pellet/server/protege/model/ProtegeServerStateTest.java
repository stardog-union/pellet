package com.clarkparsia.pellet.server.protege.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.clarkparsia.pellet.server.Environment;
import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.protege.ProtegeServerTest;
import com.clarkparsia.pellet.server.protege.TestProtegeServerConfiguration;
import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.exception.OWLServerException;
import org.semanticweb.owlapi.model.IRI;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ProtegeServerStateTest extends ProtegeServerTest {

	ProtegeServerState mServerState;

	static {
		Environment.setHome(Paths.get(".test-home"));
	}

	public ProtegeServerStateTest() {
		super();
	}

	@Before
	public void before() throws Exception {
		super.before();
		mServerState = new ProtegeServerState(new TestProtegeServerConfiguration());
	}

	@Test
	public void shouldBeEmpty() throws Exception {
		assertNotNull(mServerState);

		try {
			assertTrue(mServerState.isEmpty());
		}
		finally {
			mServerState.close();
		}
	}

	private void loadOntologies(final Client theClient) throws OWLServerException {
		// create ontologies
		createOwl2Ontology(theClient);
		createAgenciesOntology(theClient);
	}

	private Path getOntologyHEAD(final OntologyState theState) throws IOException {
		return ((ProtegeOntologyState) theState).getOntologyDirectory().resolve("HEAD");
	}

	private Path getOntologyReasoner(final OntologyState theState) throws IOException {
		return ((ProtegeOntologyState) theState).getOntologyDirectory()
		            .resolve("reasoner_state.bin");
	}

	@Test
	public void shouldHaveOntologies() throws Exception {
		assertNotNull(mServerState);
		try {
			Client aClient = mServerState.getClient();

			// create ontologies
			loadOntologies(aClient);

			// when the ontologies are created/modified after ServerState instantiation we have to
			// refresh the state.
			mServerState.reload();

			assertFalse(mServerState.isEmpty());

			Optional<OntologyState> aOwl2State = mServerState.getOntology(IRI.create("http://www.example.org/test"));
			assertNotNull(aOwl2State);
			assertTrue(aOwl2State.isPresent());
			Optional<OntologyState> aAgencyState = mServerState.getOntology(IRI.create("http://www.owl-ontologies.com/unnamed.owl"));
			assertNotNull(aAgencyState);
			assertTrue(aAgencyState.isPresent());
		}
		finally {
			mServerState.close();
			Environment.cleanHome();
		}

	}

	@Test
	public void shouldSaveOntologyStates() throws Exception {
		assertNotNull(mServerState);
		try {
			Client aClient = mServerState.getClient();

			loadOntologies(aClient);

			mServerState.reload();
			mServerState.save();

			assertFalse(mServerState.isEmpty());

			for (OntologyState aState : mServerState.ontologies()) {
				assertTrue(Files.exists(getOntologyHEAD(aState)));
				assertTrue(Files.exists(getOntologyReasoner(aState)));
			}
		}
		finally {
			mServerState.close();
			Environment.cleanHome();
		}
	}

	@Test
	public void shouldSaveAndLoadOntologyStates() throws Exception {
		assertNotNull(mServerState);
		try {
			Client aClient = mServerState.getClient();

			loadOntologies(aClient);

			mServerState.reload();
			mServerState.save();

			assertFalse(mServerState.isEmpty());

			for (OntologyState aState : mServerState.ontologies()) {
				assertTrue(Files.exists(getOntologyHEAD(aState)));
				assertTrue(Files.exists(getOntologyReasoner(aState)));
			}

			mServerState.close();
			mServerState = new ProtegeServerState(aClient, true /* strict mode */);

			assertFalse(mServerState.isEmpty());

			for (OntologyState aState : mServerState.ontologies()) {
				assertTrue(Files.exists(getOntologyHEAD(aState)));
				assertTrue(Files.exists(getOntologyReasoner(aState)));
			}
		}
		finally {
			mServerState.close();
			Environment.cleanHome();
		}
	}

}
