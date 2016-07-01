package com.clarkparsia.pellet.server.protege.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.protege.ProtegeServerTest;
import com.clarkparsia.pellet.server.protege.TestProtegeServerConfiguration;
import com.google.common.base.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.protege.editor.owl.client.LocalHttpClient;
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
		recreateServerState();
	}

	@After
	public void after() throws Exception {
		super.after();

		if (mServerState != null) {
			mServerState.close();
		}
	}

	private void recreateServerState(String... ontologies) throws Exception {
		if (mServerState != null) {
			mServerState.close();
		}
		mServerState = new ProtegeServerState(new TestProtegeServerConfiguration(ontologies));
	}

	@Test
	public void shouldBeEmpty() throws Exception {
		assertNotNull(mServerState);

		assertTrue(mServerState.ontologies().isEmpty());
	}

	private void loadOntologies(final LocalHttpClient theClient) throws Exception {
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

	private void assertOntologies(String... ontologies) {
		assertEquals(ontologies.length, mServerState.ontologies().size());

		for (String ontology : ontologies) {
			IRI ontologyIRI = IRI.create(ontology);
			Optional<OntologyState> state = mServerState.getOntology(ontologyIRI);
			assertTrue(state.isPresent());
			assertEquals(ontologyIRI, state.get().getIRI());
		}
	}

	@Test
	public void shouldHaveOntologies() throws Exception {
		assertNotNull(mServerState);

		LocalHttpClient aClient = mServerState.getClient();

		// create ontologies
		loadOntologies(aClient);

		recreateServerState(OWL2_ONT, AGENCIES_ONT);

		assertOntologies("http://www.example.org/test", "http://www.owl-ontologies.com/unnamed.owl");
	}

	@Test
	public void addRemoveOntologies() throws Exception {
		assertNotNull(mServerState);

		LocalHttpClient aClient = mServerState.getClient();

		// create ontologies
		loadOntologies(aClient);

		OntologyState s = mServerState.addOntology(OWL2_ONT);
		assertOntologies("http://www.example.org/test");
		assertNotNull(s);

		s = mServerState.addOntology(AGENCIES_ONT);
		assertOntologies("http://www.example.org/test", "http://www.owl-ontologies.com/unnamed.owl");
		assertNotNull(s);

		boolean removed = mServerState.removeOntology(IRI.create("http://www.example.org/test"));
		assertOntologies("http://www.owl-ontologies.com/unnamed.owl");
		assertTrue(removed);

		removed = mServerState.removeOntology(IRI.create("http://www.example.com/does-not-exist"));
		assertOntologies("http://www.owl-ontologies.com/unnamed.owl");
		assertFalse(removed);

		removed = mServerState.removeOntology(IRI.create("http://www.owl-ontologies.com/unnamed.owl"));
		assertOntologies();
		assertTrue(removed);
	}

	@Test
	public void shouldSaveOntologyStates() throws Exception {
		assertNotNull(mServerState);

		LocalHttpClient aClient = mServerState.getClient();

		loadOntologies(aClient);

		recreateServerState(OWL2_ONT, AGENCIES_ONT);

		mServerState.save();

		assertFalse(mServerState.ontologies().isEmpty());

		for (OntologyState aState : mServerState.ontologies()) {
			assertTrue(Files.exists(getOntologyHEAD(aState)));
			assertTrue(Files.exists(getOntologyReasoner(aState)));
		}
	}

	@Test
	public void shouldSaveAndLoadOntologyStates() throws Exception {
		assertNotNull(mServerState);

		LocalHttpClient aClient = mServerState.getClient();

		loadOntologies(aClient);

		recreateServerState(OWL2_ONT, AGENCIES_ONT);

		mServerState.save();

		assertFalse(mServerState.ontologies().isEmpty());

		for (OntologyState aState : mServerState.ontologies()) {
			assertTrue(Files.exists(getOntologyHEAD(aState)));
			assertTrue(Files.exists(getOntologyReasoner(aState)));
		}

		recreateServerState(OWL2_ONT, AGENCIES_ONT);

		assertFalse(mServerState.ontologies().isEmpty());

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
