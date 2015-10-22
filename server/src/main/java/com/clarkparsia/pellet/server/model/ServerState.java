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

	Optional<OntologyState> getOntology(IRI ontology);

	Set<OntologyState> ontologies();

	boolean isEmpty();

	void refresh();

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
		public void refresh() {
			// no-op
		}

		@Override
		public void close() throws Exception {
		}
	};
}