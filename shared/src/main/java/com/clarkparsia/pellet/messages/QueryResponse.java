package com.clarkparsia.pellet.messages;

import java.util.Objects;

import com.clarkparsia.pellet.Message;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class QueryResponse implements Message {

	private final NodeSet<? extends OWLObject> results;

	public QueryResponse(final NodeSet<? extends OWLObject> theResults) {
		results = theResults;
	}

	public NodeSet<? extends OWLObject> getResults() {
		return results;
	}

	@Override
	public boolean equals(final Object theOther) {
		if (this == theOther) {
			return true;
		}
		if (!(theOther instanceof QueryResponse)) {
			return false;
		}

		QueryResponse otherQR = (QueryResponse) theOther;
		return Objects.equals(this.results, otherQR.results);
	}

}
