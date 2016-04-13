package com.clarkparsia.pellet.server.handlers;

import com.clarkparsia.pellet.server.model.ServerState;
import com.google.inject.Inject;

/**
 * @author Evren Sirin
 */
public class ReasonerDeleteHandler extends ReasonerUpdateHandler {
	@Inject
	public ReasonerDeleteHandler(final ServerState theServerState) {
		super(theServerState, false);
	}
}
