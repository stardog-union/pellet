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
public interface OntologyState extends AutoCloseable {

	ClientState getClient(String clientID);

	IRI getIRI();

	/**
	 * Update the ontology with the latest changes from the backing store. Details of retrieving the changes depends on the backing store.
	 */
	void update();

	/**
	 * Saves the current reasoning state.
	 */
	void save();
}