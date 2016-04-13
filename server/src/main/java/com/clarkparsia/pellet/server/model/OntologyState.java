// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.server.model;

import java.util.UUID;

import org.semanticweb.owlapi.model.IRI;

/**
 * @author Evren Sirin
 */
public interface OntologyState extends AutoCloseable {
	/**
	 * Returns the client state for the given ID.
	 */
	ClientState getClient(UUID clientID);

	/**
	 * Returns the IRI of the ontology.
	 */
	IRI getIRI();

	/**
	 * Updates the ontology with the latest changes from the backing store. Details of retrieving the changes depends on the backing store.
	 */
	boolean update();

	/**
	 * Saves the current reasoning state.
	 */
	void save();

	/**
	 * Closes this ontology state and disposes the asscoaited reasoner and client states.
	 */
	@Override
	void close();
}