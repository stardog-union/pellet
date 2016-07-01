package com.complexible.pellet.client;

import com.clarkparsia.pellet.server.PelletServerTest;
import org.junit.Before;
import org.protege.editor.owl.client.LocalHttpClient;

/**
 * @author Edgar Rodriguez-Diaz
 */
public abstract class PelletClientTest extends PelletServerTest {
	protected PelletServiceProvider serviceProvider = new PelletServiceProvider(PelletService.DEFAULT_LOCAL_ENDPOINT, 0, 0, 0); // disable all timeouts for tests

	protected LocalHttpClient mClient;

	@Before
	public void before() throws Exception {
		super.before();

		mClient = new LocalHttpClient(PROTEGE_USERNAME, PROTEGE_PASSWORD, "http://" + PROTEGE_HOST + ":" + PROTEGE_PORT);
	}
}
