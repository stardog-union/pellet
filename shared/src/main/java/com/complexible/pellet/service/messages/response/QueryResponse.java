package com.complexible.pellet.service.messages.response;

import com.complexible.pellet.service.messages.AbstractJsonMessage;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class QueryResponse extends AbstractJsonMessage {

	private final NodeSet<? extends OWLObject> results;

	public QueryResponse(final NodeSet<? extends OWLObject> theResults) {
		results = theResults;
	}

	public NodeSet<? extends OWLObject> getResults() {
		return results;
	}
}
