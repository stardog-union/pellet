package com.clarkparsia.pellet;

import com.complexible.pellet.service.messages.Message;
import com.google.protobuf.ByteString;

/**
 * @author Edgar Rodriguez-Diaz
 */
public abstract class ProtoMessage implements Message {

	String MIME_TYPE = "application/x-protobuf";

	public abstract ByteString encode();

	@Override
	public String getMimeType() {
		return MIME_TYPE;
	}
}
