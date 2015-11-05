package com.complexible.pellet.client.reasoner;

import java.io.IOException;
import java.util.Set;

import com.complexible.pellet.client.ClientException;
import com.complexible.pellet.client.api.PelletService;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
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
		return null;
	}

	@Override
	public Set<Set<OWLAxiom>> explain(final OWLAxiom axiom, final int limit) {
		return null;
	}

	@Override
	public void update(final Set<OWLAxiom> additions, final Set<OWLAxiom> removals) {

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
