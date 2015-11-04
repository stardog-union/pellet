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

	public ExplainRequest(final OWLAxiom theOwlAxiom) {
		axiom = theOwlAxiom;
	}

	public OWLAxiom getAxiom() {
		return axiom;
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

		return Objects.deepEquals(axiom, otherER.axiom);
	}

	@Override
	public int hashCode() {
		return Objects.hash(axiom);
	}

	@Override
	public ByteString encode() {
		final Messages.ExplainRequest aExplainRequest = Messages.ExplainRequest.newBuilder()
		                                                                       .setAxiom(ProtoTools.toRawObject(axiom))
		                                                                       .build();
		return aExplainRequest.toByteString();
	}
}
