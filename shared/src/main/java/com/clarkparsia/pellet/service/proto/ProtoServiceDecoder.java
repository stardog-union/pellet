package com.clarkparsia.pellet.service.proto;

import java.util.Set;

import com.clarkparsia.pellet.service.MessageDecoder;
import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.messages.ExplainRequest;
import com.clarkparsia.pellet.service.messages.ExplainResponse;
import com.clarkparsia.pellet.service.messages.QueryRequest;
import com.clarkparsia.pellet.service.messages.QueryResponse;
import com.clarkparsia.pellet.service.messages.UpdateRequest;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * @author Edgar Rodriguez-Diaz
 */
public final class ProtoServiceDecoder implements ServiceDecoder {

	private final String MEDIA_TYPE = "application/x-protobuf";

	@Override
	public String getMediaType() {
		return MEDIA_TYPE;
	}

	@Override
	public boolean canDecode(final String theMediaType) {
		return MEDIA_TYPE.equals(theMediaType);
	}

	@Override
	public QueryRequest queryRequest(final byte[] theBytes) {
		return new QueryRequestDecoder().decode(theBytes);
	}

	@Override
	public ExplainRequest explainRequest(final byte[] theBytes) {
		return new ExplainRequestDecoder().decode(theBytes);
	}

	@Override
	public UpdateRequest updateRequest(final byte[] theBytes) {
		return new UpdateRequestDecoder().decode(theBytes);
	}

	@Override
	public QueryResponse queryResponse(final byte[] theBytes) {
		return new QueryResponseDecoder().decode(theBytes);
	}

	@Override
	public ExplainResponse explainResponse(final byte[] theBytes) {
		return new ExplainResponseDecoder().decode(theBytes);
	}

	private static final class QueryRequestDecoder implements MessageDecoder<QueryRequest> {
		@Override
		public QueryRequest decode(final byte[] theBytes) {
			try {
				final Messages.QueryRequest aProtoReq = Messages.QueryRequest.parseFrom(theBytes);
				final OWLLogicalEntity anEntity = ProtoTools.fromOwlObject(aProtoReq.getInput());

				return new QueryRequest(anEntity);

			}
			catch (Exception e) {
				// TODO: create decoding exception
				Throwables.propagate(e);
			}
			return null;
		}
	}

	private static final class ExplainRequestDecoder implements MessageDecoder<ExplainRequest> {
		@Override
		public ExplainRequest decode(final byte[] theBytes) {
			try {
				final Messages.ExplainRequest aProtoReq = Messages.ExplainRequest.parseFrom(theBytes);
				final OWLAxiom anAxiom = ProtoTools.fromOwlObject(aProtoReq.getAxiom());

				return new ExplainRequest(anAxiom);
			}
			catch (Exception e) {
				// TODO: create decoding exception
				Throwables.propagate(e);
			}
			return null;
		}
	}

	private static final class UpdateRequestDecoder implements MessageDecoder<UpdateRequest> {
		@Override
		public UpdateRequest decode(final byte[] theBytes) {
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

	private static final class QueryResponseDecoder implements MessageDecoder<QueryResponse> {

		@Override
		public QueryResponse decode(final byte[] theBytes) {
			try {
				final Messages.QueryResponse aQueryResp = Messages.QueryResponse.parseFrom(theBytes);
				final NodeSet<? extends OWLObject> nodeSet = ProtoTools.fromNodeSet(aQueryResp.getResult());

				return new QueryResponse(nodeSet);
			}
			catch (Exception e) {
				Throwables.propagate(e);
			}
			return null;
		}
	}

	private static final class ExplainResponseDecoder implements MessageDecoder<ExplainResponse> {
		@Override
		public ExplainResponse decode(final byte[] theBytes) {
			try {
				final Messages.ExplainResponse aExplainResp = Messages.ExplainResponse.parseFrom(theBytes);
				final Set<Set<OWLAxiom>> axioms = Sets.newHashSet();

				for (Messages.AxiomSet aAxiomSet : aExplainResp.getAxiomsetsList()) {
					axioms.add(ProtoTools.fromAxiomSet(aAxiomSet));
				}

				return new ExplainResponse(axioms);
			}
			catch (Exception e) {
				Throwables.propagate(e);
			}
			return null;
		}
	}
}
