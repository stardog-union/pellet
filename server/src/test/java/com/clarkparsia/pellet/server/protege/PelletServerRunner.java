package com.clarkparsia.pellet.server.protege;

import com.clarkparsia.pellet.server.PelletServer;
import com.clarkparsia.pellet.server.PelletServerTest;
import com.google.common.base.Throwables;
import org.protege.owl.server.api.client.Client;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class PelletServerRunner extends PelletServerTest {

	public void run() {
		try {
			new PelletServer(injector).start();
		}
		catch (Exception e) {
			Throwables.propagate(e);
		}
	}

	public static void main(String[] args) {
		PelletServerTest.beforeClass();

		new PelletServerRunner().run();
	}

	@Override
	public Client provideClient() throws Exception {
		return createClient(RMI_PORT, REDMOND);
	}
}
