package com.clarkparsia.pellet.server;

import com.clarkparsia.pellet.server.protege.ProtegeServerTest;
import com.google.inject.Guice;
import com.google.inject.util.Modules;
import org.junit.After;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class PelletServerTest extends ProtegeServerTest {
	protected static PelletServer pelletServer;

	public void startPelletServer(String... ontologies) throws Exception {
		pelletServer = new PelletServer(Guice.createInjector(Modules.override(new PelletServerModule())
		                                                            .with(new TestModule(ontologies))));
		pelletServer.start();
	}

	public void stopPelletServer() {
		if (pelletServer != null) {
			pelletServer.stop();
			pelletServer = null;
		}
	}

	@After
	public void after() throws Exception {
		stopPelletServer();
		super.after();
	}

}
