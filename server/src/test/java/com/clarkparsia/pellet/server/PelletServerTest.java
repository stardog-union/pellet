package com.clarkparsia.pellet.server;

import com.clarkparsia.pellet.server.protege.ProtegeServerTest;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author Edgar Rodriguez-Diaz
 */
public abstract class PelletServerTest extends ProtegeServerTest {

	protected static Injector injector;
	protected static PelletServer pelletServer;

	@BeforeClass
	public static void beforeClass() {
		injector = Guice.createInjector(Modules.override(new PelletServerModule())
		                                       .with(new TestModule()));

		pelletServer = new PelletServer(injector);
	}

	@Before
	public void before() throws Exception {
		super.before();
		pelletServer.start();
	}

	@After
	public void after() {
		pelletServer.stop();
		super.after();
	}

}
