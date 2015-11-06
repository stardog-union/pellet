package com.clarkparsia.pellet.server.exceptions;

/**
 * Server exceptions hold a error code which makes it easier to identify when serialized.
 *
 * @author Edgar Rodriguez-Diaz
 */
public class ServerException extends Exception {

	protected final int mErrorCode;

	public ServerException() {
		this(500);   // borrowing from HTTP status code 500
	}

	public ServerException(final int theErrorCode) {
		super();
		mErrorCode = theErrorCode;
	}

	public ServerException(final int theErrorCode, final String theMessage) {
		super(theMessage);
		mErrorCode = theErrorCode;
	}

	public ServerException(final int theErrorCode, final String theMessage, final Throwable theCause) {
		super(theMessage, theCause);
		mErrorCode = theErrorCode;
	}

	public ServerException(final int theErrorCode, final Throwable theCause) {
		super(theCause);
		mErrorCode = theErrorCode;
	}

	public int getErrorCode() {
		return mErrorCode;
	}
}
