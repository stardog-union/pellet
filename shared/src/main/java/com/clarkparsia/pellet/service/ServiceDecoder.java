package com.clarkparsia.pellet.service;

import com.clarkparsia.pellet.service.messages.ExplainRequest;
import com.clarkparsia.pellet.service.messages.ExplainResponse;
import com.clarkparsia.pellet.service.messages.QueryRequest;
import com.clarkparsia.pellet.service.messages.QueryResponse;
import com.clarkparsia.pellet.service.messages.UpdateRequest;
import com.google.protobuf.ByteString;

/**
 * @author Edgar Rodriguez-Diaz
 */
public interface ServiceDecoder {

	String getMediaType();

	boolean canDecode(final String theMediaType);

	QueryRequest queryRequest(final byte[] theBytes);

	ExplainRequest explainRequest(final byte[] theBytes);

	UpdateRequest updateRequest(final byte[] theBytes);

	QueryResponse queryResponse(final byte[] theBytes);

	ExplainResponse explainResponse(final byte[] theBytes);
}
