// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.complexible.pellet.client.reasoner;

import com.clarkparsia.pellet.service.reasoner.SchemaReasonerFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * @author Evren Sirin
 */
public class SchemaOWLReasonerFactory implements OWLReasonerFactory {
	private final SchemaReasonerFactory factory;

	public SchemaOWLReasonerFactory(final SchemaReasonerFactory theFactory) {
		factory = theFactory;
	}

	@Override
	public String getReasonerName() {
		return "Pellet Schema Reasoner";
	}

	
	@Override
	public OWLReasoner createNonBufferingReasoner(final OWLOntology ontology) {
		return new SchemaOWLReasoner(ontology, factory, BufferingMode.NON_BUFFERING);
	}

	@Override
	public OWLReasoner createReasoner(final OWLOntology ontology) {
		return new SchemaOWLReasoner(ontology, factory, BufferingMode.BUFFERING);
	}

	@Override
	public OWLReasoner createNonBufferingReasoner(final OWLOntology ontology,  final OWLReasonerConfiguration theOWLReasonerConfiguration) {
		return createNonBufferingReasoner(ontology);
	}

	@Override
	public OWLReasoner createReasoner(final OWLOntology ontology, final OWLReasonerConfiguration theOWLReasonerConfiguration) {
		return createReasoner(ontology);
	}

	@Override
	public String toString() {
		return getReasonerName();
	}
}