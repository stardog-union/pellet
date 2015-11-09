package com.clarkparsia.pellet.server.protege.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.clarkparsia.pellet.server.Environment;
import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.server.protege.ProtegeServerStateProvider;
import com.clarkparsia.pellet.server.protege.ProtegeServerTest;
import com.clarkparsia.pellet.server.protege.TestProtegeServerConfiguration;
import com.google.common.base.Optional;
import org.junit.Test;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.RemoteOntologyDocument;
import org.protege.owl.server.api.exception.OWLServerException;
import org.protege.owl.server.util.ClientUtilities;
import org.semanticweb.owlapi.model.IRI;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ProtegeServerStateTest extends ProtegeServerTest {

	ProtegeServerStateProvider mServerStateProvider;

	static {
		Environment.setHome(Paths.get(".test-home"));
	}

	public ProtegeServerStateTest() {
		super();
		mServerStateProvider = new ProtegeServerStateProvider(new TestProtegeServerConfiguration());
	}

	@Test
	public void shouldBeEmpty() throws Exception {
		ServerState aServerState = mServerStateProvider.get();
		assertNotNull(aServerState);

		try {
			assertTrue(aServerState.isEmpty());
		}
		finally {
			aServerState.close();
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
		ProtegeServerState aServerState = (ProtegeServerState) mServerStateProvider.get();
		assertNotNull(aServerState);
		try {
			Client aClient = aServerState.getClient();

			// create ontologies
			loadOntologies(aClient);

			// when the ontologies are created/modified after ServerState instantiation we have to
			// refresh the state.
			aServerState.reload();

			assertFalse(aServerState.isEmpty());

			Optional<OntologyState> aOwl2State = aServerState.getOntology(IRI.create("http://www.example.org/test"));
			assertNotNull(aOwl2State);
			assertTrue(aOwl2State.isPresent());
			Optional<OntologyState> aAgencyState = aServerState.getOntology(IRI.create("http://www.owl-ontologies.com/unnamed.owl"));
			assertNotNull(aAgencyState);
			assertTrue(aAgencyState.isPresent());
		}
		finally {
			aServerState.close();
			Environment.cleanHome();
		}

	}

	@Test
	public void shouldSaveOntologyStates() throws Exception {
		ProtegeServerState aServerState = (ProtegeServerState) mServerStateProvider.get();
		assertNotNull(aServerState);
		try {
			Client aClient = aServerState.getClient();

			loadOntologies(aClient);

			aServerState.reload();
			aServerState.save();

			assertFalse(aServerState.isEmpty());

			for (OntologyState aState : aServerState.ontologies()) {
				assertTrue(Files.exists(getOntologyHEAD(aState)));
				assertTrue(Files.exists(getOntologyReasoner(aState)));
			}
		}
		finally {
			aServerState.close();
			Environment.cleanHome();
		}
	}

	@Test
	public void shouldSaveAndLoadOntologyStates() throws Exception {
		ProtegeServerState aServerState = (ProtegeServerState) mServerStateProvider.get();
		assertNotNull(aServerState);
		try {
			Client aClient = aServerState.getClient();

			loadOntologies(aClient);

			aServerState.reload();
			aServerState.save();

			assertFalse(aServerState.isEmpty());

			for (OntologyState aState : aServerState.ontologies()) {
				assertTrue(Files.exists(getOntologyHEAD(aState)));
				assertTrue(Files.exists(getOntologyReasoner(aState)));
			}

			aServerState.close();
			aServerState = new ProtegeServerState(aClient, true /* strict mode */);

			assertFalse(aServerState.isEmpty());

			for (OntologyState aState : aServerState.ontologies()) {
				assertTrue(Files.exists(getOntologyHEAD(aState)));
				assertTrue(Files.exists(getOntologyReasoner(aState)));
			}
		}
		finally {
			aServerState.close();
			Environment.cleanHome();
		}
	}

}
