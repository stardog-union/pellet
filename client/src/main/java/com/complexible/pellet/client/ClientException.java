package com.complexible.pellet.client;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ClientException extends Exception {

	public ClientException() {
		this("Client Exception");
	}

	public ClientException(final String theMessage) {
		super(theMessage);
	}

	public ClientException(final String theMessage, final Throwable theCause) {
		super(theMessage, theCause);
	}
}
