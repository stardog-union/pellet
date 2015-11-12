package com.complexible.pellet.client;

import java.nio.file.Paths;

import com.clarkparsia.pellet.server.Environment;
import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.protege.model.ProtegeServerState;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.clarkparsia.pellet.service.io.EncodingException;
import com.clarkparsia.pellet.service.json.GenericJsonMessage;
import com.clarkparsia.pellet.service.messages.UpdateRequest;
import com.clarkparsia.pellet.service.proto.ProtoServiceEncoder;
import com.complexible.pellet.client.api.PelletService;
import com.complexible.pellet.client.reasoner.RemoteSchemaReasoner;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.protege.owl.server.api.client.Client;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import retrofit.Call;

import static org.junit.Assert.assertEquals;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class PelletServiceTest extends PelletClientTest {

	static {
		Environment.setHome(Paths.get(".test-home"));
	}

	PelletServiceProvider serviceProvider = new PelletServiceProvider(PelletService.DEFAULT_LOCAL_ENDPOINT,
	                                                                  0, 0, 0); // disable all timeouts for tests

	private IRI ontologyId;

	@Before
	public void before() throws Exception {
		super.before();

		// reset client in protege server state
		final ProtegeServerState aServerState = (ProtegeServerState) pelletServer.getState();

		// create test ontology
		ontologyId = createAgenciesOntology(aServerState.getClient());

		restartServer();

		// force reload server state
		pelletServer.getState().reload();
	}

	private void restartServer() throws Exception {
		stopPelletServer();
		startPelletServer();
	}

	@Override
	public Client provideClient() throws Exception {
		return createClient(RMI_PORT, REDMOND);
	}

	@After
	public void after() {
		super.after();
		Environment.cleanHome();
	}

	@Test
	public void shouldPutUpdateWithEmptySets() throws EncodingException {
		PelletService aService = serviceProvider.get();
		UpdateRequest updateReq = new UpdateRequest(ImmutableSet.<OWLAxiom>of(), ImmutableSet.<OWLAxiom>of());
		ServiceEncoder encoder = new ProtoServiceEncoder();
		RequestBody aBody = RequestBody.create(MediaType.parse(encoder.getMediaType()),
		                                       encoder.encode(updateReq));

		Call<GenericJsonMessage> updateCall = aService.update(ontologyId,
		                                                      RemoteSchemaReasoner.CLIENT_ID,
		                                                      GenericJsonMessage.MIME_TYPE,
		                                                      aBody);
		GenericJsonMessage aMsg = ClientTools.executeCall(updateCall);

		assertEquals("Update successful.", aMsg.message);
	}

	@Test
	public void shouldGetVersionFromClient() {
		PelletService aService = serviceProvider.get();

		Call<JsonObject> aVersionCall = aService.version(ontologyId,
		                                                 RemoteSchemaReasoner.CLIENT_ID,
		                                                 GenericJsonMessage.MIME_TYPE);

		JsonObject aJsonObj = ClientTools.executeCall(aVersionCall);

		assertEquals(1, aJsonObj.get("version").getAsInt());
	}

}
