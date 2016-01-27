package com.clarkparsia.pellet.server.jobs;

import java.util.logging.Logger;

import com.clarkparsia.pellet.server.model.ServerState;

/**
 * Job definition for reloading Server State.
 *
 * @author Edgar Rodriguez-Diaz
 */
public final class ServerStateUpdate implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(ServerStateUpdate.class.getName());

	private final ServerState serverState;

	public ServerStateUpdate(final ServerState theServerState) {
		serverState = theServerState;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		LOGGER.info("Checking for updated ontologies...");
		boolean updated = serverState.update();
		if (updated) {
			LOGGER.info("Ontology updates are complete");
		}
		else {
			LOGGER.info("No ontologies were updated");
		}
	}
}