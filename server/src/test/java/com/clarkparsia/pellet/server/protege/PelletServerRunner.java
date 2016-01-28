package com.clarkparsia.pellet.server.protege;

import java.nio.file.Paths;

import com.clarkparsia.pellet.server.PelletServer;
import com.clarkparsia.pellet.server.PelletServerModule;
import com.clarkparsia.pellet.server.PelletServerTest;
import com.google.inject.Guice;
import org.protege.owl.server.api.client.Client;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class PelletServerRunner extends PelletServerTest {

	public void run() throws Exception {
		ProtegeServerConfiguration aHomeConfig = new ProtegeServerConfiguration(TEST_HOME.resolve("server.properties").toFile());
		pelletServer = new PelletServer(Guice.createInjector(new PelletServerModule(aHomeConfig)));
		pelletServer.start();
	}

	public static void main(String[] args) throws Exception {
		new PelletServerRunner().run();
	}
}
