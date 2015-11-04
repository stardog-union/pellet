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

	private final SchemaReasoner.QueryType queryType;

	private final OWLLogicalEntity input;

	public QueryRequest(final SchemaReasoner.QueryType theQueryType,
	                    final OWLLogicalEntity theInput) {
		queryType = theQueryType;
		input = theInput;
	}

	public SchemaReasoner.QueryType getQueryType() {
		return queryType;
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

		return Objects.equals(queryType, otherQR.queryType) &&
		       Objects.equals(input, otherQR.input);
	}

	@Override
	public int hashCode() {
		return Objects.hash(queryType, input);
	}

	@Override
	public ByteString encode() {
		final Messages.QueryRequest aProtoReq = Messages.QueryRequest.newBuilder()
		                                                             .setType(encodeQueryType(queryType))
		                                                             .setInput(ProtoTools.toRawObject(input))
		                                                             .build();
		return aProtoReq.toByteString();
	}

	private static Messages.QueryRequest.QueryType encodeQueryType(SchemaReasoner.QueryType theType) {
		switch(theType) {
			case EQUIVALENT:
				return Messages.QueryRequest.QueryType.EQUIVALENT;
			case CHILD:
				return Messages.QueryRequest.QueryType.CHILD;
			case PARENT:
				return Messages.QueryRequest.QueryType.PARENT;
			case DESCENDANT:
				return Messages.QueryRequest.QueryType.DESCENDANT;
			case ANCESTOR:
				return Messages.QueryRequest.QueryType.ANCESTOR;
			case DISJOINT:
				return Messages.QueryRequest.QueryType.DISJOINT;
			case INVERSE:
				return Messages.QueryRequest.QueryType.INVERSE;
			case DOMAIN:
				return Messages.QueryRequest.QueryType.DOMAIN;
			case RANGE:
				return Messages.QueryRequest.QueryType.RANGE;
			default:
				return null;
		}
	}
}
