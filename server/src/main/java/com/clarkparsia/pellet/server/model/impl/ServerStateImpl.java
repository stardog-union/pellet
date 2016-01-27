// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.server.model.impl;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.model.ServerState;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Immutable implementation of a ontology server state.
 *
 * @author Evren Sirin
 */
public class ServerStateImpl implements ServerState {
	protected final OWLOntologyManager manager;

	private final Map<IRI, OntologyState> ontologies;

	protected ServerStateImpl(final Iterable<OntologyState> onts) {
		manager = OWLManager.createOWLOntologyManager();

		ontologies = Maps.newConcurrentMap();
		for (OntologyState ontoState : onts) {
			ontologies.put(ontoState.getIRI(), ontoState);
		}
	}

	@Override
	public Optional<OntologyState> getOntology(IRI ontology) {
		return Optional.fromNullable(ontologies.get(ontology));
	}

	protected OntologyState createOntologyState(final String ontologyPath) throws OWLOntologyCreationException {
		OWLOntology ont = manager.loadOntologyFromOntologyDocument(new File(ontologyPath));
		return new OntologyStateImpl(ont);
	}

	@Override
	public OntologyState addOntology(final String ontologyPath) throws OWLOntologyCreationException {
		OntologyState state = createOntologyState(ontologyPath);
		ontologies.put(state.getIRI(), state);
		return state;
	}

	@Override
	public boolean removeOntology(final IRI ontology) {
		OntologyState state = ontologies.remove(ontology);
		boolean removed = (state != null);
		if (removed) {
			state.close();
		}
		return removed;
	}

	@Override
	public Collection<OntologyState> ontologies() {
		return Collections.unmodifiableCollection(ontologies.values());
	}

	@Override
	public boolean update() {
		boolean updated = false;
		for (OntologyState ontState : ontologies()) {
			updated |= ontState.update();
		}
		return updated;
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