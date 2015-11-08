package com.clarkparsia.pellet.service.proto;

import java.io.IOException;
import java.util.Set;

import com.clarkparsia.pellet.service.MessageEncoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.clarkparsia.pellet.service.io.EncodingException;
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

	public byte[] encode(final QueryRequest theQueryRequest) throws EncodingException {
		return new QueryRequestEncoder().encode(theQueryRequest);
	}

	public byte[] encode(final ExplainRequest theExplainRequest) throws EncodingException {
		return new ExplainRequestEncoder().encode(theExplainRequest);
	}

	public byte[] encode(final UpdateRequest theUpdateRequest) throws EncodingException {
		return new UpdateRequestEncoder().encode(theUpdateRequest);
	}

	public byte[] encode(final QueryResponse theQueryResponse) throws EncodingException {
		return new QueryResponseEncoder().encode(theQueryResponse);
	}

	public byte[] encode(final ExplainResponse theExplainResponse) throws EncodingException {
		return new ExplainResponseEncoder().encode(theExplainResponse);
	}

	private static final class QueryRequestEncoder implements MessageEncoder<QueryRequest> {

		@Override
		public byte[] encode(final QueryRequest theQueryRequest) throws EncodingException {
			try {
				final Messages.QueryRequest aProtoReq = Messages.QueryRequest.newBuilder()
				                                                             .setInput(ProtoTools.toOwlObject(theQueryRequest
						                                                                                              .getInput()))
				                                                             .build();
				return aProtoReq.toByteArray();
			}
			catch (IOException theE) {
				throw new EncodingException(theE);
			}
		}
	}

	private static final class ExplainRequestEncoder implements MessageEncoder<ExplainRequest> {

		@Override
		public byte[] encode(final ExplainRequest theObject) throws EncodingException {
			try {
				final Messages.ExplainRequest aExplainRequest = Messages.ExplainRequest.newBuilder()
				                                                                       .setAxiom(ProtoTools.toOwlObject(theObject
						                                                                                                        .getAxiom()))
				                                                                       .build();
				return aExplainRequest.toByteArray();
			}
			catch (IOException theE) {
				throw new EncodingException(theE);
			}
		}
	}

	private static final class UpdateRequestEncoder implements MessageEncoder<UpdateRequest> {

		@Override
		public byte[] encode(final UpdateRequest theObject) throws EncodingException {
			try {
				final Messages.UpdateRequest aUpdateReq = Messages.UpdateRequest.newBuilder()
				                                                                .setAdditions(ProtoTools.toAxiomSet(theObject
						                                                                                                    .getAdditions()))
				                                                                .setRemovals(ProtoTools.toAxiomSet(theObject
						                                                                                                   .getRemovals()))
				                                                                .build();

				return aUpdateReq.toByteArray();
			}
			catch (IOException theE) {
				throw new EncodingException(theE);
			}
		}
	}

	private static final class QueryResponseEncoder implements MessageEncoder<QueryResponse> {

		@Override
		public byte[] encode(final QueryResponse theObject) throws EncodingException {
			try {
				final Messages.QueryResponse aQueryResponse = Messages.QueryResponse.newBuilder()
				                                                                    .setResult(ProtoTools.toNodeSet(theObject.getResults()))
				                                                                    .build();
				return aQueryResponse.toByteArray();
			}
			catch (IOException theE) {
				throw new EncodingException(theE);
			}
		}
	}

	private static final class ExplainResponseEncoder implements MessageEncoder<ExplainResponse> {

		@Override
		public byte[] encode(final ExplainResponse theObject) throws EncodingException {
			try {
				final Messages.ExplainResponse.Builder aExplainResp = Messages.ExplainResponse.newBuilder();

				int i = 0;
				for (Set<OWLAxiom> aAxiomSet : theObject.getAxiomSets()) {
					aExplainResp.addAxiomsets(i++, ProtoTools.toAxiomSet(aAxiomSet));
				}

				return aExplainResp.build().toByteArray();
			}
			catch (IOException theE) {
				throw new EncodingException(theE);
			}
		}
	}

}
