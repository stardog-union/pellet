package com.clarkparsia.pellet.server.protege.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.protege.ProtegeServerTest;
import com.clarkparsia.pellet.server.protege.TestProtegeServerConfiguration;
import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.exception.OWLServerException;
import org.semanticweb.owlapi.model.IRI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ProtegeServerStateTest extends ProtegeServerTest {

	ProtegeServerState mServerState;

	public ProtegeServerStateTest() {
		super();
	}

	@Before
	public void before() throws Exception {
		super.before();
		reloadServerState("dummy.history");
	}

	@Before
	public void after() throws Exception {
		super.after();

		if (mServerState != null) {
			mServerState.close();
		}
	}

	private void reloadServerState(String... ontologies) throws Exception {
		if (mServerState != null) {
			mServerState.close();
		}
		mServerState = new ProtegeServerState(new TestProtegeServerConfiguration(ontologies));
	}

	@Test
	public void shouldBeEmpty() throws Exception {
		assertNotNull(mServerState);

		assertTrue(mServerState.isEmpty());
	}

	private void loadOntologies(final Client theClient) throws OWLServerException {
		// create ontologies
		createOwl2Ontology(theClient);
		createAgenciesOntology(theClient);
	}

	private Path getOntologyHEAD(final OntologyState theState) throws IOException {
		return ((ProtegeOntologyState) theState).getPath().resolveSibling("HEAD");
	}

	private Path getOntologyReasoner(final OntologyState theState) throws IOException {
		return ((ProtegeOntologyState) theState).getPath();
	}

	@Test
	public void shouldHaveOntologies() throws Exception {
		assertNotNull(mServerState);

		Client aClient = mServerState.getClient();

		// create ontologies
		loadOntologies(aClient);

		// when the ontologies are created/modified after ServerState instantiation we have to
		// refresh the state.
		reloadServerState(OWL2_HISTORY, AGENCIES_HISTORY);

		assertFalse(mServerState.isEmpty());

		Optional<OntologyState> aOwl2State = mServerState.getOntology(IRI.create("http://www.example.org/test"));
		assertNotNull(aOwl2State);
		assertTrue(aOwl2State.isPresent());
		Optional<OntologyState> aAgencyState = mServerState.getOntology(IRI.create("http://www.owl-ontologies.com/unnamed.owl"));
		assertNotNull(aAgencyState);
		assertTrue(aAgencyState.isPresent());
	}

	@Test
	public void shouldSaveOntologyStates() throws Exception {
		assertNotNull(mServerState);

		Client aClient = mServerState.getClient();

		loadOntologies(aClient);

		reloadServerState(OWL2_HISTORY, AGENCIES_HISTORY);

		mServerState.save();

		assertFalse(mServerState.isEmpty());

		for (OntologyState aState : mServerState.ontologies()) {
			assertTrue(Files.exists(getOntologyHEAD(aState)));
			assertTrue(Files.exists(getOntologyReasoner(aState)));
		}
	}

	@Test
	public void shouldSaveAndLoadOntologyStates() throws Exception {
		assertNotNull(mServerState);

		Client aClient = mServerState.getClient();

		loadOntologies(aClient);

		reloadServerState(OWL2_HISTORY, AGENCIES_HISTORY);

		mServerState.save();

		assertFalse(mServerState.isEmpty());

		for (OntologyState aState : mServerState.ontologies()) {
			assertTrue(Files.exists(getOntologyHEAD(aState)));
			assertTrue(Files.exists(getOntologyReasoner(aState)));
		}

		reloadServerState(OWL2_HISTORY, AGENCIES_HISTORY);

		assertFalse(mServerState.isEmpty());

		int requiredChecks = 0;
		for (OntologyState aState : mServerState.ontologies()) {
			assertTrue(Files.exists(getOntologyHEAD(aState)));
			assertTrue(Files.exists(getOntologyReasoner(aState)));
			requiredChecks++;
		}

		// check that the 2 loaded ontologies exist
		assertEquals(2, requiredChecks);
	}

}
