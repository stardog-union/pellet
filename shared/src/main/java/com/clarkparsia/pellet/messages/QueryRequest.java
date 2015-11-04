package com.clarkparsia.pellet.messages;

import java.util.Objects;

import com.clarkparsia.pellet.Messages;
import com.clarkparsia.pellet.ProtoMessage;
import com.complexible.pellet.service.reasoner.SchemaReasoner;
import com.google.protobuf.ByteString;
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

	@Override
	public ByteString encode() {
		final Messages.QueryRequest aProtoReq = Messages.QueryRequest.newBuilder()
		                                                             .setInput(ProtoTools.toRawObject(input))
		                                                             .build();
		return aProtoReq.toByteString();
	}
}
