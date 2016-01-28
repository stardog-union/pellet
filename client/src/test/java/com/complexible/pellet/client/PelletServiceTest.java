package com.complexible.pellet.client;

import java.util.UUID;

import com.clarkparsia.pellet.service.ServiceEncoder;
import com.clarkparsia.pellet.service.io.EncodingException;
import com.clarkparsia.pellet.service.messages.UpdateRequest;
import com.clarkparsia.pellet.service.proto.ProtoServiceEncoder;
import com.complexible.pellet.client.api.PelletService;
import com.complexible.pellet.client.reasoner.RemoteSchemaReasoner;
import com.google.common.collect.ImmutableSet;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.junit.Before;
import org.junit.Test;
import org.protege.owl.server.api.client.Client;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import retrofit2.Call;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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

		startPelletServer(AGENCIES_HISTORY);
	}

	@Test
	public void shouldUpdateWithEmptySets() throws EncodingException {
		PelletService aService = serviceProvider.get();
		UpdateRequest updateReq = new UpdateRequest(ImmutableSet.<OWLAxiom>of(), ImmutableSet.<OWLAxiom>of());
		ServiceEncoder encoder = new ProtoServiceEncoder();
		RequestBody aBody = RequestBody.create(MediaType.parse(encoder.getMediaType()),
		                                       encoder.encode(updateReq));

		Call<Void> updateCall = aService.update(agencyOntId, ID, aBody);
		ClientTools.executeCall(updateCall);
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

		ClientTools.executeCall(aService.add(OWL2_HISTORY));

		ClientTools.executeCall(aService.remove(owl2OntId));

		Response<Void> aResp = aService.remove(owl2OntId).execute();
		assertEquals(400, aResp.code());
		assertEquals("Ontology not found: " + owl2OntId, aResp.message());
	}
}
