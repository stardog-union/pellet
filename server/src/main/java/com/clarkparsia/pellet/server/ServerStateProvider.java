package com.clarkparsia.pellet.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.model.ServerState;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ServerStateProvider implements Provider<ServerState> {

	private static final Logger LOGGER = Logger.getLogger(ServerStateProvider.class.getName());

	private ServerStateFactory mServerStateFactory;

	@Inject
	public ServerStateProvider(final ServerStateFactory theServerStateFactory) {
		mServerStateFactory = theServerStateFactory;
	}

	@Override
	public ServerState get() {
		try {
			return mServerStateFactory.getInstance();
		}
		catch (ServerException e) {
			LOGGER.log(Level.SEVERE, "Couldn't get a StateServer from the Provider", e);
		}
		return ServerState.EMPTY;
	}
}
