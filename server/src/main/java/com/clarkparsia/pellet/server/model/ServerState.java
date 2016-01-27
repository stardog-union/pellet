// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.server.model;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * @author Evren Sirin
 */
public interface ServerState extends AutoCloseable {
	/**
	 * Return the stste associated with the given IRI.
	 */
	Optional<OntologyState> getOntology(IRI ontology);

	boolean removeOntology(IRI ontology);

	OntologyState addOntology(String ontologyPath) throws OWLOntologyCreationException;

	/**
	 * Returns all the ontologies managed by this server.
	 */
	Collection<OntologyState> ontologies();

	/**
	 * Update all ontology states. This function will iterate over all the {@link #ontologies() ontologies} and {@link OntologyState#update() update} each one.
	 */
	boolean update();

	/**
	 * Persists the state.
	 */
	void save();
}