package com.clarkparsia.pellet;

import java.util.Set;

import com.clarkparsia.pellet.messages.ExplainRequest;
import com.clarkparsia.pellet.messages.ProtoTools;
import com.clarkparsia.pellet.messages.QueryRequest;
import com.clarkparsia.pellet.messages.UpdateRequest;
import com.complexible.pellet.service.reasoner.SchemaReasoner;
import com.google.common.base.Throwables;
import com.google.protobuf.ByteString;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalEntity;

/**
 * @author Edgar Rodriguez-Diaz
 */
public final class MessageDecoders {

	public static QueryRequest queryRequest(final ByteString theBytes) {
		return QueryRequestDecoder.INSTANCE.decode(theBytes);
	}

	public static ExplainRequest explainRequest(final ByteString theBytes) {
		return ExplainRequestDecoder.INSTANCE.decode(theBytes);
	}

	public static UpdateRequest updateRequest(final ByteString theBytes) {
		return UpdateRequestDecoder.INSTANCE.decode(theBytes);
	}

	private enum QueryRequestDecoder implements MessageDecoder<QueryRequest> {
		INSTANCE;

		@Override
		public QueryRequest decode(final ByteString theBytes) {
			try {
				final Messages.QueryRequest aProtoReq = Messages.QueryRequest.parseFrom(theBytes.toByteArray());
				final SchemaReasoner.QueryType aQueryType = decodeQueryType(aProtoReq.getType());
				final OWLLogicalEntity anEntity = ProtoTools.fromRawObject(aProtoReq.getInput());

				return new QueryRequest(aQueryType, anEntity);

			}
			catch (Exception e) {
				// TODO: create decoding exception
				Throwables.propagate(e);
			}
			return null;
		}

		private static SchemaReasoner.QueryType decodeQueryType(Messages.QueryRequest.QueryType theType) {
			switch(theType) {
				case EQUIVALENT:
					return SchemaReasoner.QueryType.EQUIVALENT;
				case CHILD:
					return SchemaReasoner.QueryType.CHILD;
				case PARENT:
					return SchemaReasoner.QueryType.PARENT;
				case DESCENDANT:
					return SchemaReasoner.QueryType.DESCENDANT;
				case ANCESTOR:
					return SchemaReasoner.QueryType.ANCESTOR;
				case DISJOINT:
					return SchemaReasoner.QueryType.DISJOINT;
				case INVERSE:
					return SchemaReasoner.QueryType.INVERSE;
				case DOMAIN:
					return SchemaReasoner.QueryType.DOMAIN;
				case RANGE:
					return SchemaReasoner.QueryType.RANGE;
				default:
					return null;
			}
		}
	}

	private enum ExplainRequestDecoder implements MessageDecoder<ExplainRequest> {
		INSTANCE;

		@Override
		public ExplainRequest decode(final ByteString theBytes) {
			try {
				final Messages.ExplainRequest aProtoReq = Messages.ExplainRequest.parseFrom(theBytes);
				final int limit = aProtoReq.getLimit();
				final OWLAxiom anAxiom = ProtoTools.fromRawObject(aProtoReq.getAxiom());

				return new ExplainRequest(anAxiom, limit);
			}
			catch (Exception e) {
				// TODO: create decoding exception
				Throwables.propagate(e);
			}
			return null;
		}
	}

	private enum UpdateRequestDecoder implements MessageDecoder<UpdateRequest> {
		INSTANCE;

		@Override
		public UpdateRequest decode(final ByteString theBytes) {
			try {
				final Messages.UpdateRequest aProtoReq = Messages.UpdateRequest.parseFrom(theBytes);
				final Set<OWLAxiom> additions = ProtoTools.fromAxiomSet(aProtoReq.getAdditions());
				final Set<OWLAxiom> removals = ProtoTools.fromAxiomSet(aProtoReq.getRemovals());

				return new UpdateRequest(additions, removals);
			}
			catch (Exception e) {
				Throwables.propagate(e);
			}
			return null;
		}
	}
}
