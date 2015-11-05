package com.clarkparsia.pellet;

import com.clarkparsia.pellet.messages.ExplainRequest;
import com.clarkparsia.pellet.messages.ExplainResponse;
import com.clarkparsia.pellet.messages.QueryRequest;
import com.clarkparsia.pellet.messages.QueryResponse;
import com.clarkparsia.pellet.messages.UpdateRequest;

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
