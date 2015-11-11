package com.clarkparsia.pellet.server.protege;

import com.clarkparsia.pellet.server.PelletServer;
import com.clarkparsia.pellet.server.PelletServerTest;
import com.google.common.base.Throwables;

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
}
