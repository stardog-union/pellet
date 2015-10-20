// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.server.model;

import org.semanticweb.owlapi.model.IRI;

/**
 * @author Evren Sirin
 */
public interface ServerState extends AutoCloseable {

	OntologyState getOntology(IRI ontology);

	void refresh();

	ServerState EMPTY = new ServerState() {
		@Override
		public OntologyState getOntology(final IRI ontology) {
			return null;
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