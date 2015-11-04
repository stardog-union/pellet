// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.server.model;

import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyChange;

/**
 * @author Evren Sirin
 */
public interface OntologyState extends AutoCloseable {
	ClientState createClient(String clientID);

	ClientState getClient(String clientID);

	IRI getOntologyIRI();

	void update(Set<OWLAxiom> additions, Set<OWLAxiom> removals);

	void reload();

	void save();
}