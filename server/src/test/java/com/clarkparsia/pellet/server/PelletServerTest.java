package com.clarkparsia.pellet.server;

import com.clarkparsia.pellet.server.protege.ProtegeServerTest;
import com.google.inject.Guice;
import com.google.inject.util.Modules;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Edgar Rodriguez-Diaz
 */
public abstract class PelletServerTest extends ProtegeServerTest {

	static PelletServer pelletServer = new PelletServer(Guice.createInjector(Modules.override(new PelletServerModule())
	                                                                                .with(new TestModule())));

	@BeforeClass
	public static void beforeClass() throws Exception {
		ProtegeServerTest.beforeClass();
		pelletServer.start();
	}

	@AfterClass
	public static void afterClass() {
		pelletServer.stop();
		ProtegeServerTest.afterClass();
	}

}
