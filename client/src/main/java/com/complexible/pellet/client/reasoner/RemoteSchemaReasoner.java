package com.complexible.pellet.client.reasoner;

import java.util.Set;

import com.complexible.pellet.client.api.PelletService;
import com.complexible.pellet.service.reasoner.SchemaReasoner;
import com.google.inject.Inject;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class RemoteSchemaReasoner implements SchemaReasoner {

	final PelletService mService;

	@Inject
	public RemoteSchemaReasoner(final PelletService thePelletService) {
		mService = thePelletService;
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
}
