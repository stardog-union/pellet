package com.complexible.pellet.client;

import java.io.IOException;
import java.nio.file.Paths;

import com.clarkparsia.pellet.server.Environment;
import com.clarkparsia.pellet.service.json.GenericJsonMessage;
import com.complexible.pellet.client.api.PelletService;
import com.google.common.base.Throwables;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import retrofit.Call;
import retrofit.Response;

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

	// TODO: write low level pellet service tests here

	@Before
	public void before() throws Exception {
		super.before();
		createAgenciesOntology(createClient(RMI_PORT, REDMOND));
		pelletServer.reload();
	}

	@After
	public void after() {
		Environment.cleanHome();
	}

	@Test
	public void shouldGetVersionFromClient() {
		PelletService aService = serviceProvider.get();

		Call<JsonObject> aVersionCall = aService.version(IRI.create("http://www.owl-ontologies.com/unnamed.owl"),
		                                                 GenericJsonMessage.MIME_TYPE);

		JsonObject aJsonObj = executeCall(aVersionCall);

		assertEquals(1, aJsonObj.get("version").getAsInt());
	}

	private <O> O executeCall(final Call<O> theCall) {
		O results = null;

		try {
			Response<O> aResp = theCall.execute();

			if (!aResp.isSuccess()) {
				throw new ClientException(String.format("Request call failed: [%d] %s",
				                                        aResp.code(), aResp.message()));
			}

			results = aResp.body();
		}
		catch (IOException theE) {
			Throwables.propagate(new ClientException(theE.getMessage(), theE));
		}
		catch (ClientException theE) {
			Throwables.propagate(theE);
		}

		return results;
	}

}
