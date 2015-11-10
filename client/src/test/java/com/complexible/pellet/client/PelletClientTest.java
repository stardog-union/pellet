package com.complexible.pellet.client;

import com.clarkparsia.pellet.server.PelletServer;
import com.clarkparsia.pellet.server.PelletServerModule;
import com.clarkparsia.pellet.server.PelletServerTest;
import com.clarkparsia.pellet.server.TestModule;
import com.complexible.pellet.client.api.PelletService;
import com.google.inject.Guice;
import com.google.inject.util.Modules;
import org.junit.BeforeClass;

/**
 * @author Edgar Rodriguez-Diaz
 */
public abstract class PelletClientTest extends PelletServerTest {

	@BeforeClass
	public static void beforeClass() {
		injector = Guice.createInjector(Modules.override(new PelletServerModule(),
		                                                 new ClientModule(PelletService.DEFAULT_LOCAL_ENDPOINT,
		                                                                  0, 0, 0)) // disable timeouts for tests
		                                       .with(new TestModule()));

		pelletServer = new PelletServer(injector);
	}

}
