package com.clarkparsia.pellet.server;

import com.clarkparsia.pellet.server.protege.ProtegeServerTest;
import com.google.inject.Guice;
import com.google.inject.util.Modules;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author Edgar Rodriguez-Diaz
 */
public abstract class PelletServerTest extends ProtegeServerTest {

	static PelletServer pelletServer = new PelletServer(Guice.createInjector(Modules.override(new PelletServerModule())
	                                                                                .with(new TestModule())));

	@Before
	public void beforeClass() throws Exception {
		super.before();
		pelletServer.start();
	}

	@After
	public void afterClass() {
		pelletServer.stop();
		super.after();
	}

}
