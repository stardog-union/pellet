// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.server.model.impl;

import java.util.Map;
import java.util.Objects;

import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.model.ServerState;
import com.google.common.collect.ImmutableMap;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author Evren Sirin
 */
public class ServerStateImpl implements ServerState {
	private final Map<IRI, OntologyState> ontologies;

	public ServerStateImpl(final Iterable<OWLOntology> onts) {
		ImmutableMap.Builder<IRI, OntologyState> builder = ImmutableMap.builder();
		for (OWLOntology ontology : onts) {
			OntologyState context = new OntologyStateImpl(ontology);
			builder.put(context.getOntologyIRI(), context);
		}

		ontologies = builder.build();
	}

	@Override
	public OntologyState getOntology(IRI ontology) {
		return Objects.requireNonNull(ontologies.get(ontology), "Ontology not found: " + ontology);
	}

	@Override
	public void close() throws Exception {
		for (OntologyState ontology : ontologies.values()) {
			ontology.close();
		}
	}
}