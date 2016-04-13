package com.clarkparsia.pellet.server.handlers;

import com.clarkparsia.pellet.server.model.ServerState;
import com.google.inject.Inject;

/**
 * @author Evren Sirin
 */
public class ReasonerInsertHandler extends ReasonerUpdateHandler {
	@Inject
	public ReasonerInsertHandler(final ServerState theServerState) {
		super(theServerState, true);
	}
}
