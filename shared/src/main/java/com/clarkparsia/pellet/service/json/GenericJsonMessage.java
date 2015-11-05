package com.clarkparsia.pellet.service.json;

/**
 * Generic simple JSON message.
 *
 * @author Edgar Rodriguez-Diaz
 */
public class GenericJsonMessage extends AbstractJsonMessage {

	public final String message;

	public GenericJsonMessage(final String theMessage) {
		message = theMessage;
	}

}
