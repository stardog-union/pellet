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
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.model.ServerState;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.semanticweb.owlapi.model.IRI;

/**
 * Immutable implementation of a ontology server state.
 *
 * @author Evren Sirin
 */
public class ServerStateImpl implements ServerState {
	private final AtomicReference<Map<IRI, OntologyState>> ontologies = new AtomicReference<Map<IRI, OntologyState>>();

	private ServerStateImpl(final Iterable<OntologyState> onts) {
		ImmutableMap.Builder<IRI, OntologyState> builder = ImmutableMap.builder();
		for (OntologyState ontoState : onts) {
			builder.put(ontoState.getIRI(), ontoState);
		}

		ontologies.set(builder.build());
	}

	@Override
	public Optional<OntologyState> getOntology(IRI ontology) {
		return Optional.<OntologyState>of(Objects.requireNonNull(ontologies.get()
		                                                                   .get(ontology),
		                                                         "Ontology not found: " + ontology));
	}

	@Override
	public Set<OntologyState> ontologies() {
		return ImmutableSet.copyOf(ontologies.get().values());
	}

	@Override
	public boolean isEmpty() {
		return ontologies.get().isEmpty();
	}

	@Override
	public void refresh() {
		// no-op: this implementation doesn't have an explicit source for the ontologies, it just
		// takes whatever it is in the constructor parameters
	}

	@Override
	public void close() throws Exception {
		for (OntologyState ontology : ontologies.get().values()) {
			ontology.close();
		}
	}

	public static ServerState create(final Iterable<OntologyState> onts) {
		return new ServerStateImpl(onts);
	}
}