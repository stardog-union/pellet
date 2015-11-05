package com.complexible.pellet.service.messages;

import com.clarkparsia.pellet.Message;

/**
 * @author Edgar Rodriguez-Diaz
 */
public interface JsonMessage extends Message {

	String MIME_TYPE = "application/json";

	/**
	 * Returns a JSON Object string representation of the message.
	 *
	 * @return  the string representation of a JSON object
	 */
	String toJsonString();
}
