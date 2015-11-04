package com.clarkparsia.pellet;

import com.complexible.pellet.service.messages.Message;

/**
 * @author Edgar Rodriguez-Diaz
 */
public abstract class ProtoMessage implements Message {

	String MIME_TYPE = "application/x-protobuf";

	@Override
	public String getMimeType() {
		return MIME_TYPE;
	}
}
