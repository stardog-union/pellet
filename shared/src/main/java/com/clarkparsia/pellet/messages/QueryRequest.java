package com.clarkparsia.pellet.messages;

import java.util.Objects;

import com.clarkparsia.pellet.ProtoMessage;
import org.semanticweb.owlapi.model.OWLLogicalEntity;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class QueryRequest extends ProtoMessage {

	private final OWLLogicalEntity input;

	public QueryRequest(final OWLLogicalEntity theInput) {
		input = theInput;
	}

	public OWLLogicalEntity getInput() {
		return input;
	}

	@Override
	public boolean equals(final Object theOther) {
		if (this == theOther) {
			return true;
		}
		if (!(theOther instanceof QueryRequest)) {
			return false;
		}

		QueryRequest otherQR = (QueryRequest) theOther;

		return Objects.equals(input, otherQR.input);
	}

	@Override
	public int hashCode() {
		return Objects.hash(input);
	}
}
