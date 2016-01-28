package com.complexible.pellet.client;

import com.clarkparsia.pellet.server.PelletServerTest;
import com.complexible.pellet.client.api.PelletService;
import org.protege.owl.server.api.client.Client;

/**
 * @author Edgar Rodriguez-Diaz
 */
public abstract class PelletClientTest extends PelletServerTest {
	protected PelletServiceProvider serviceProvider = new PelletServiceProvider(PelletService.DEFAULT_LOCAL_ENDPOINT, 0, 0, 0); // disable all timeouts for tests

}
