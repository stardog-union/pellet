// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.server.model.impl;

import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.model.ServerState;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.semanticweb.owlapi.model.IRI;

/**
 * Immutable implementation of a ontology server state.
 *
 * @author Evren Sirin
 */
public class ServerStateImpl implements ServerState {
	private final ImmutableMap<IRI, OntologyState> ontologies;

	protected ServerStateImpl(final Iterable<OntologyState> onts) {
		ImmutableMap.Builder<IRI, OntologyState> builder = ImmutableMap.builder();
		for (OntologyState ontoState : onts) {
			builder.put(ontoState.getIRI(), ontoState);
		}

		ontologies = builder.build();
	}

	@Override
	public Optional<OntologyState> getOntology(IRI ontology) {
		return Optional.fromNullable(ontologies.get(ontology));
	}

	@Override
	public Iterable<OntologyState> ontologies() {
		return ontologies.values();
	}

	@Override
	public boolean isEmpty() {
		return ontologies.isEmpty();
	}

	@Override
	public void update() {
		for (OntologyState ontState : ontologies()) {
			ontState.update();
		}
	}

	@Override
	public void save() {
		for (OntologyState aOntoState : ontologies()) {
			aOntoState.save();
		}
	}

	@Override
	public void close() throws Exception {
		for (OntologyState ontology : ontologies()) {
			ontology.close();
		}
	}
}