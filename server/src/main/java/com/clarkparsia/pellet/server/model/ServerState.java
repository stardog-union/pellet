// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.server.model;

import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import org.semanticweb.owlapi.model.IRI;

/**
 * @author Evren Sirin
 */
public interface ServerState extends AutoCloseable {
	/**
	 * Return the stste associated with the given IRI.
	 */
	Optional<OntologyState> getOntology(IRI ontology);

	/**
	 * Returns all the ontologies managed by this server.
	 */
	Iterable<OntologyState> ontologies();

	boolean isEmpty();

	/**
	 * Reload the latest version of the ontologies from the repository and classify from scratch.
	 */
	void reload();

	/**
	 * Update all ontology states. This function will iterate over all the {@link #ontologies() ontologies} and {@link OntologyState#update() update} each one.
	 */
	void update();

	ServerState EMPTY = new ServerState() {
		@Override
		public Optional<OntologyState> getOntology(final IRI ontology) {
			return Optional.absent();
		}

		@Override
		public Set<OntologyState> ontologies() {
			return ImmutableSet.of();
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public void reload() {
			// no-op
		}

		@Override
		public void update() {
			// no-op
		}

		@Override
		public void close() throws Exception {
		}
	};
}