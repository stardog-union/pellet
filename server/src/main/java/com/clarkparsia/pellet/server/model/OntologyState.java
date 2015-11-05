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

import com.google.common.base.Function;
import org.protege.owl.server.api.ChangeHistory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

/**
 * @author Evren Sirin
 */
public interface OntologyState extends AutoCloseable {
	ClientState createClient(String clientID);

	ClientState getClient(String clientID);

	IRI getIRI();

	void update(Function<OWLOntology, List<OWLOntologyChange>> changeSupplier);

	void save();
}