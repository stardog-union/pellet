package com.clarkparsia.pellet;

import com.clarkparsia.pellet.messages.ExplainRequest;
import com.clarkparsia.pellet.messages.ProtoTools;
import com.clarkparsia.pellet.messages.QueryRequest;
import com.clarkparsia.pellet.messages.UpdateRequest;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class MessageEncoders {

	public static byte[] encode(final QueryRequest theQueryRequest) {
		return new QueryRequestEncoder().encode(theQueryRequest);
	}

	public static byte[] encode(final ExplainRequest theExplainRequest) {
		return new ExplainRequestEncoder().encode(theExplainRequest);
	}

	public static byte[] encode(final UpdateRequest theUpdateRequest) {
		return new UpdateRequestEncoder().encode(theUpdateRequest);
	}

	private static class QueryRequestEncoder implements MessageEncoder<QueryRequest> {

		@Override
		public byte[] encode(final QueryRequest theObject) {
			final Messages.QueryRequest aProtoReq = Messages.QueryRequest.newBuilder()
			                                                             .setInput(ProtoTools.toRawObject(theObject.getInput()))
			                                                             .build();
			return aProtoReq.toByteString()
			                .toByteArray();
		}
	}

	private static class ExplainRequestEncoder implements MessageEncoder<ExplainRequest> {

		@Override
		public byte[] encode(final ExplainRequest theObject) {
			final Messages.ExplainRequest aExplainRequest = Messages.ExplainRequest.newBuilder()
			                                                                       .setAxiom(ProtoTools.toRawObject(theObject.getAxiom()))
			                                                                       .build();
			return aExplainRequest.toByteString().toByteArray();
		}
	}

	private static class UpdateRequestEncoder implements MessageEncoder<UpdateRequest> {

		@Override
		public byte[] encode(final UpdateRequest theObject) {
			Messages.UpdateRequest aUpdateReq = Messages.UpdateRequest.newBuilder()
			                                                           .setAdditions(ProtoTools.toAxiomSet(theObject.getAdditions()))
			                                                           .setRemovals(ProtoTools.toAxiomSet(theObject.getRemovals()))
			                                                           .build();

			return aUpdateReq.toByteString().toByteArray();
		}
	}

}
