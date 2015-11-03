package com.complexible.pellet.client.reasoner;

import java.io.IOException;
import java.util.Set;

import com.complexible.pellet.client.ClientException;
import com.complexible.pellet.client.api.PelletService;
import com.complexible.pellet.service.messages.request.ExplainRequest;
import com.complexible.pellet.service.messages.request.QueryRequest;
import com.complexible.pellet.service.messages.request.UpdateRequest;
import com.complexible.pellet.service.messages.response.ExplainResponse;
import com.complexible.pellet.service.messages.response.QueryResponse;
import com.complexible.pellet.service.messages.response.UpdateResponse;
import com.complexible.pellet.service.reasoner.SchemaReasoner;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.NodeSet;
import retrofit.Call;
import retrofit.Response;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class RemoteSchemaReasoner implements SchemaReasoner {

	final PelletService mService;
	final IRI mOntologyIri;

	@Inject
	public RemoteSchemaReasoner(final PelletService thePelletService,
	                            final IRI theOntologyIri) {
		Preconditions.checkNotNull(theOntologyIri, "the Ontology must not be Null.");

		mService = thePelletService;
		mOntologyIri = theOntologyIri;
	}

	@Override
	public <T extends OWLObject> NodeSet<T> query(final QueryType theQueryType, final OWLLogicalEntity input) {
		final Call<QueryResponse> aQueryCall = mService.query(mOntologyIri,
		                                                      new QueryRequest(theQueryType, input));

		return (NodeSet<T>) executeCall(aQueryCall).getResults();
	}

	@Override
	public Set<Set<OWLAxiom>> explain(final OWLAxiom axiom, final int limit) {
		final Call<ExplainResponse> aExplainCall = mService.explain(mOntologyIri,
		                                                            new ExplainRequest(axiom, limit));

		return executeCall(aExplainCall).getResults();
	}

	@Override
	public void update(final Set<OWLAxiom> additions, final Set<OWLAxiom> removals) {
		final Call<UpdateResponse> aUpdateCall = mService.update(mOntologyIri,
		                                                         new UpdateRequest(additions, removals));

		executeCall(aUpdateCall);
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
