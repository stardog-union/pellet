package com.complexible.pellet.service;

import java.io.Serializable;

import com.google.gson.Gson;

/**
 * Simple message to be serialized.
 *
 * @author Edgar Rodriguez-Diaz
 */
public class Message implements Serializable {

	public final String message;

	public Message(final String theMessage) {
		message = theMessage;
	}

	public String toJsonString() {
		return (new Gson()).toJson(this);
	}
}
