package com.clarkparsia.pellet.messages;

import java.util.Objects;

import com.clarkparsia.pellet.Messages;
import com.clarkparsia.pellet.ProtoMessage;
import com.google.protobuf.ByteString;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ExplainRequest extends ProtoMessage {

	private final OWLAxiom axiom;

	private final int limit;

	public ExplainRequest(final OWLAxiom theOwlAxiom, final int theLimit) {
		axiom = theOwlAxiom;
		limit = theLimit;
	}

	public OWLAxiom getAxiom() {
		return axiom;
	}

	public int getLimit() {
		return limit;
	}

	@Override
	public boolean equals(final Object theOther) {
		if (this == theOther) {
			return true;
		}
		if (!(theOther instanceof ExplainRequest)) {
			return false;
		}

		ExplainRequest otherER = (ExplainRequest) theOther;

		return Objects.deepEquals(axiom, otherER.axiom) && limit == otherER.limit;
	}

	@Override
	public int hashCode() {
		return Objects.hash(limit, axiom);
	}

	@Override
	public ByteString encode() {
		final Messages.ExplainRequest aExplainRequest = Messages.ExplainRequest.newBuilder()
		                                                                       .setLimit(limit)
		                                                                       .setAxiom(ProtoTools.toRawObject(axiom))
		                                                                       .build();
		return aExplainRequest.toByteString();
	}
}
