package com.clarkparsia.pellet;

import com.clarkparsia.pellet.messages.ExplainRequest;
import com.clarkparsia.pellet.messages.ExplainResponse;
import com.clarkparsia.pellet.messages.QueryRequest;
import com.clarkparsia.pellet.messages.QueryResponse;
import com.clarkparsia.pellet.messages.UpdateRequest;
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
