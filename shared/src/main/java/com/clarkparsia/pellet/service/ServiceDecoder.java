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

	QueryRequest queryRequest(final ByteString theBytes);

	ExplainRequest explainRequest(final ByteString theBytes);

	UpdateRequest updateRequest(final ByteString theBytes);

	QueryResponse queryResponse(final ByteString theBytes);

	ExplainResponse explainResponse(final ByteString theBytes);
}
