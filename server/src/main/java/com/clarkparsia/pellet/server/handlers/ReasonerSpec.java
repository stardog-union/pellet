package com.clarkparsia.pellet.server.handlers;

import java.util.Set;

import com.clarkparsia.pellet.server.PelletServer;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;

/**
 * @author Edgar Rodriguez-Diaz
 */
public abstract class ReasonerSpec implements PathHandlerSpec {

	protected final Set<ServiceDecoder> mDecoders;
	protected final Set<ServiceEncoder> mEncoders;

	public static String REASONER_PATH = PelletServer.ROOT_PATH + "reasoner";

	protected final ServerState mServerState;

	public ReasonerSpec(final ServerState theServerState,
	                    final Set<ServiceDecoder> theDecoders,
	                    final Set<ServiceEncoder> theEncoders) {
		mServerState = theServerState;
		mDecoders = theDecoders;
		mEncoders = theEncoders;
	}

	protected String path(final String theUrlPart) {
		return REASONER_PATH + "/" + theUrlPart;
	}
}
