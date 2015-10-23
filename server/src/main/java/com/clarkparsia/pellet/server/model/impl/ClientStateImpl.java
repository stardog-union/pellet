// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.server.model.impl;

import java.util.List;

import com.clarkparsia.modularity.IncremantalReasonerFactory;
import com.clarkparsia.pellet.server.model.ClientState;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * @author Evren Sirin
 */
public class ClientStateImpl implements ClientState {
	private final OWLReasoner reasoner;
	private final OWLOntology ontology;

	public ClientStateImpl(final OWLOntology theOntology) {
		ontology = theOntology;
		reasoner = IncremantalReasonerFactory.getInstance().createReasoner(ontology);
	}

	@Override
	public IRI getOntologyIRI() {
		return  ontology.getOntologyID().getOntologyIRI().get();
	}

	@Override
	public OWLReasoner getReasoner() {
		return reasoner;
	}

	@Override
	public void applyChanges(final List<? extends OWLOntologyChange> changes) {
		ontology.getOWLOntologyManager().applyChanges(changes);
	}

	@Override
	public void close() {
		reasoner.dispose();
	}
}