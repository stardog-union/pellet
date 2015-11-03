package com.complexible.pellet.service.messages.request;

import com.complexible.pellet.service.messages.AbstractJsonMessage;
import com.complexible.pellet.service.reasoner.SchemaReasoner;
import org.semanticweb.owlapi.model.OWLLogicalEntity;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class QueryRequest extends AbstractJsonMessage {

	private final SchemaReasoner.QueryType queryType;

	private final OWLLogicalEntity input;

	public QueryRequest(final SchemaReasoner.QueryType theQueryType, final OWLLogicalEntity theInput) {
		queryType = theQueryType;
		input = theInput;
	}

	public SchemaReasoner.QueryType getQueryType() {
		return queryType;
	}

	public OWLLogicalEntity getInput() {
		return input;
	}
}
