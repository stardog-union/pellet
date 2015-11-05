package com.clarkparsia.pellet.service;

import com.clarkparsia.pellet.service.messages.ExplainRequest;
import com.clarkparsia.pellet.service.messages.ExplainResponse;
import com.clarkparsia.pellet.service.messages.QueryRequest;
import com.clarkparsia.pellet.service.messages.QueryResponse;
import com.clarkparsia.pellet.service.messages.UpdateRequest;

/**
 * @author Edgar Rodriguez-Diaz
 */
public interface ServiceEncoder {

	String getMediaType();

	boolean canEncode(final String theMediaType);

	byte[] encode(final QueryRequest theQueryRequest);

	byte[] encode(final ExplainRequest theExplainRequest);

	byte[] encode(final UpdateRequest theUpdateRequest);

	byte[] encode(final QueryResponse theQueryResponse);

	byte[] encode(final ExplainResponse theExplainResponse);

}
