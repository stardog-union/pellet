package com.clarkparsia.pellet.service.json;

import com.google.gson.Gson;

/**
 * @author Edgar Rodriguez-Diaz
 */
public abstract class AbstractJsonMessage implements JsonMessage {

	public String getMimeType() {
		return JsonMessage.MIME_TYPE;
	}

	@Override
	public String toJsonString() {
		return (new Gson()).toJson(this);
	}

}
