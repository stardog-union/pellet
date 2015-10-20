package com.clarkparsia.pellet.server.exceptions;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ProtegeConnectionException extends ServerException {

	private static int ERROR_CODE = 100;

	public ProtegeConnectionException() {
		super(ERROR_CODE);
	}

	public ProtegeConnectionException(final String theMessage) {
		super(ERROR_CODE, theMessage);
	}

	public ProtegeConnectionException(final String theMessage, final Throwable theCause) {
		super(ERROR_CODE, theMessage, theCause);
	}

	public ProtegeConnectionException(final Throwable theCause) {
		super(ERROR_CODE, theCause);
	}
}
