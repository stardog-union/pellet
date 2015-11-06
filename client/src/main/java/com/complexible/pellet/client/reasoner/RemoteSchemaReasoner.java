package com.complexible.pellet.client.reasoner;

import java.io.IOException;
import java.util.Set;

import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.clarkparsia.pellet.service.json.GenericJsonMessage;
import com.clarkparsia.pellet.service.messages.ExplainRequest;
import com.clarkparsia.pellet.service.messages.ExplainResponse;
import com.clarkparsia.pellet.service.messages.QueryRequest;
import com.clarkparsia.pellet.service.messages.QueryResponse;
import com.clarkparsia.pellet.service.messages.UpdateRequest;
import com.clarkparsia.pellet.service.proto.ProtoServiceDecoder;
import com.clarkparsia.pellet.service.proto.ProtoServiceEncoder;
import com.complexible.pellet.client.ClientException;
import com.complexible.pellet.client.api.PelletService;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;
import retrofit.Call;
import retrofit.Response;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class RemoteSchemaReasoner implements SchemaReasoner {

	final PelletService mService;
	final IRI mOntologyIri;

	// TODO: provide this via guice and some configurable parameters for more formats
	final ServiceEncoder mEncoder = new ProtoServiceEncoder();
	final ServiceDecoder mDecoder = new ProtoServiceDecoder();

	@Inject
	public RemoteSchemaReasoner(final PelletService thePelletService,
	                            @Assisted final OWLOntology theOntology) {
		Preconditions.checkNotNull(theOntology, "the Ontology must not be Null.");

		mService = thePelletService;

		assert theOntology.getOntologyID().getOntologyIRI().isPresent();

		mOntologyIri = theOntology.getOntologyID()
		                          .getOntologyIRI()
		                          .get();
	}

	@Override
	public <T extends OWLObject> NodeSet<T> query(final QueryType theQueryType, final OWLLogicalEntity input) {
		RequestBody aReqBody = RequestBody.create(MediaType.parse(mEncoder.getMediaType()),
		                                       mEncoder.encode(new QueryRequest(input)));

		Call<ResponseBody> queryCall = mService.query(mOntologyIri, theQueryType, aReqBody);
		try {
			final ResponseBody aRespBody = executeCall(queryCall);
			QueryResponse queryResponse = mDecoder.queryResponse(aRespBody.bytes());

			return (NodeSet<T>) queryResponse.getResults();
		}
		catch (IOException e) {
			Throwables.propagate(e);
		}
		return null;
	}

	@Override
	public Set<Set<OWLAxiom>> explain(final OWLAxiom axiom, final int limit) {
		RequestBody aReqBody = RequestBody.create(MediaType.parse(mEncoder.getMediaType()),
		                                          mEncoder.encode(new ExplainRequest(axiom)));

		Call<ResponseBody> explainCall = mService.explain(mOntologyIri, limit, aReqBody);
		try {
			final ResponseBody aRespBody = executeCall(explainCall);
			ExplainResponse explainResponse = mDecoder.explainResponse(aRespBody.bytes());

			return explainResponse.getAxiomSets();
		}
		catch (IOException e) {
			Throwables.propagate(e);
		}
		return null;
	}

	@Override
	public void update(final Set<OWLAxiom> additions, final Set<OWLAxiom> removals) {
		RequestBody aReqBody = RequestBody.create(MediaType.parse(mEncoder.getMediaType()),
		                                          mEncoder.encode(new UpdateRequest(additions, removals)));

		Call<GenericJsonMessage> updateCall = mService.update(mOntologyIri, aReqBody);
		executeCall(updateCall);
	}

	@Override
	public void close() throws Exception {

	}

	private <O> O executeCall(final Call<O> theCall) {
		O results = null;

		try {
			Response<O> aResp = theCall.execute();

			if (aResp.isSuccess()) {
				results = aResp.body();
			}
			else {
				throw new ClientException("Request for query call failed");
			}
		}
		catch (IOException theE) {
			Throwables.propagate(new ClientException(theE.getMessage(), theE));
		}
		catch (ClientException theE) {
			Throwables.propagate(theE);
		}

		return results;
	}
}
