package com.clarkparsia.pellet.server.protege.model;

import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.server.protege.ProtegeServerStateProvider;
import com.clarkparsia.pellet.server.protege.ProtegeServerTest;
import com.clarkparsia.pellet.server.protege.TestProtegeServerConfiguration;
import com.google.common.base.Optional;
import org.junit.Test;
import org.protege.owl.server.api.client.Client;
import org.semanticweb.owlapi.model.IRI;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ProtegeServerStateTest extends ProtegeServerTest {

	ProtegeServerStateProvider mServerStateProvider;

	public ProtegeServerStateTest() {
		super();
		mServerStateProvider = new ProtegeServerStateProvider(TestProtegeServerConfiguration.INSTANCE);
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

	@Test
	public void shouldHaveOntologies() throws Exception {
		ProtegeServerState aServerState = (ProtegeServerState) mServerStateProvider.get();
		assertNotNull(aServerState);
		try {
			Client aClient = aServerState.getClient();

			// create ontologies
			createOwl2Ontology(aClient);
			createAgenciesOntology(aClient);

			// when the ontologies are created/modified after ServerState instantiation we have to
			// refresh the state.
			aServerState.refresh();

			assertFalse(aServerState.isEmpty());

			Optional<OntologyState> aOwl2State = aServerState.getOntology(IRI.create(root(aClient).toString() + "/", OWL2_HISTORY));
			assertNotNull(aOwl2State);
			assertTrue(aOwl2State.isPresent());
			Optional<OntologyState> aAgencyState = aServerState.getOntology(IRI.create(root(aClient).toString() + "/", AGENCIES_HISTORY));
			assertNotNull(aAgencyState);
			assertTrue(aAgencyState.isPresent());
		}
		finally {
			aServerState.close();
		}

	}

}
