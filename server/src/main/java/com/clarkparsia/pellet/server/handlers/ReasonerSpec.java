package com.clarkparsia.pellet.server.handlers;

import com.clarkparsia.pellet.server.PelletServer;
import com.clarkparsia.pellet.server.model.ServerState;

/**
 * @author Edgar Rodriguez-Diaz
 */
public abstract class ReasonerSpec implements PathHandlerSpec {

	public static String REASONER_PATH = PelletServer.ROOT_PATH + "/reasoner";

	protected final ServerState mServerState;

	public ReasonerSpec(final ServerState theServerState) {
		mServerState = theServerState;
	}

	protected String path(final String theUrlPart) {
		return REASONER_PATH + "/" + theUrlPart;
	}
}
