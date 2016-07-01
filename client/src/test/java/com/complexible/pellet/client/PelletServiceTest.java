package com.complexible.pellet.client;

import java.util.UUID;

import com.clarkparsia.owlapiv3.OWL;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import retrofit2.Call;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class PelletServiceTest extends PelletClientTest {
	private UUID ID = UUID.randomUUID();

	private IRI agencyOntId;
	private IRI owl2OntId;

	@Before
	public void before() throws Exception {
		super.before();

		// create test ontology
		agencyOntId = createAgenciesOntology(mClient);
		owl2OntId = createOwl2Ontology(mClient);

		startPelletServer(AGENCIES_ONT);
	}

	@Test
	public void shouldUpdateWithEmptySets() throws Exception {
		PelletService aService = serviceProvider.get();

		ClientTools.executeCall(aService.insert(agencyOntId, ID, OWL.Ontology()));
		ClientTools.executeCall(aService.delete(agencyOntId, ID, OWL.Ontology()));
	}

	@Test
	public void shouldGetVersionFromClient() {
		PelletService aService = serviceProvider.get();

		Call<Integer> aVersionCall = aService.version(agencyOntId, ID);

		int aVersion = ClientTools.executeCall(aVersionCall);

		assertEquals(1, aVersion);
	}

	@Test
	public void ontologyAddDelete() throws Exception {
		PelletService aService = serviceProvider.get();

		ClientTools.executeCall(aService.load(OWL2_ONT));

		ClientTools.executeCall(aService.unload(owl2OntId));

		Response<Void> aResp = aService.unload(owl2OntId).execute();
		assertEquals(400, aResp.code());
		assertEquals("Ontology not found: " + owl2OntId, aResp.message());
	}
}
