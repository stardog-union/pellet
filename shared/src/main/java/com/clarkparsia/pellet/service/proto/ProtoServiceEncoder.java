package com.clarkparsia.pellet.service.proto;

import java.util.Set;

import com.clarkparsia.pellet.service.MessageEncoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.clarkparsia.pellet.service.messages.ExplainRequest;
import com.clarkparsia.pellet.service.messages.ExplainResponse;
import com.clarkparsia.pellet.service.messages.QueryRequest;
import com.clarkparsia.pellet.service.messages.QueryResponse;
import com.clarkparsia.pellet.service.messages.UpdateRequest;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ProtoServiceEncoder implements ServiceEncoder {

	private final String MEDIA_TYPE = "application/x-protobuf";

	@Override
	public String getMediaType() {
		return MEDIA_TYPE;
	}

	@Override
	public boolean canEncode(final String theMediaType) {
		return MEDIA_TYPE.equals(theMediaType);
	}

	public byte[] encode(final QueryRequest theQueryRequest) {
		return new QueryRequestEncoder().encode(theQueryRequest);
	}

	public byte[] encode(final ExplainRequest theExplainRequest) {
		return new ExplainRequestEncoder().encode(theExplainRequest);
	}

	public byte[] encode(final UpdateRequest theUpdateRequest) {
		return new UpdateRequestEncoder().encode(theUpdateRequest);
	}

	public byte[] encode(final QueryResponse theQueryResponse) {
		return new QueryResponseEncoder().encode(theQueryResponse);
	}

	public byte[] encode(final ExplainResponse theExplainResponse) {
		return new ExplainResponseEncoder().encode(theExplainResponse);
	}

	private static final class QueryRequestEncoder implements MessageEncoder<QueryRequest> {

		@Override
		public byte[] encode(final QueryRequest theObject) {
			final Messages.QueryRequest aProtoReq = Messages.QueryRequest.newBuilder()
			                                                             .setInput(ProtoTools.toRawObject(theObject.getInput()))
			                                                             .build();
			return aProtoReq.toByteArray();
		}
	}

	private static final class ExplainRequestEncoder implements MessageEncoder<ExplainRequest> {

		@Override
		public byte[] encode(final ExplainRequest theObject) {
			final Messages.ExplainRequest aExplainRequest = Messages.ExplainRequest.newBuilder()
			                                                                       .setAxiom(ProtoTools.toRawObject(theObject.getAxiom()))
			                                                                       .build();
			return aExplainRequest.toByteArray();
		}
	}

	private static final class UpdateRequestEncoder implements MessageEncoder<UpdateRequest> {

		@Override
		public byte[] encode(final UpdateRequest theObject) {
			Messages.UpdateRequest aUpdateReq = Messages.UpdateRequest.newBuilder()
			                                                           .setAdditions(ProtoTools.toAxiomSet(theObject.getAdditions()))
			                                                           .setRemovals(ProtoTools.toAxiomSet(theObject.getRemovals()))
			                                                           .build();

			return aUpdateReq.toByteArray();
		}
	}

	private static final class QueryResponseEncoder implements MessageEncoder<QueryResponse> {

		@Override
		public byte[] encode(final QueryResponse theObject) {
			final Messages.QueryResponse aQueryResponse = Messages.QueryResponse.newBuilder()
			                                                                    .setResult(ProtoTools.toNodeSet(theObject.getResults()))
			                                                                    .build();
			return aQueryResponse.toByteArray();
		}
	}

	private static final class ExplainResponseEncoder implements MessageEncoder<ExplainResponse> {

		@Override
		public byte[] encode(final ExplainResponse theObject) {
			final Messages.ExplainResponse.Builder aExplainResp = Messages.ExplainResponse.newBuilder();

			int i = 0;
			for (Set<OWLAxiom> aAxiomSet : theObject.getAxiomSets()) {
				aExplainResp.addAxiomsets(i++, ProtoTools.toAxiomSet(aAxiomSet));
			}

			return aExplainResp.build().toByteArray();
		}
	}

}
