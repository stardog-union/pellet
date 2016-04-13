// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.service.reasoner;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * Simple reasoner interface for schema reasoning.
 *
 * @author Evren Sirin
 */
public interface SchemaReasoner extends AutoCloseable {

	int NO_VERSION = -1;

	/**
	 * Execute a schema query.
	 *
	 * @param query         schema query
	 * @param <T>           type of return entities
	 * @return              the query result
	 */
	<T extends OWLObject> NodeSet<T> query(SchemaQuery query);

	/**
	 * Return the explanations for the given axiom.
	 *
	 * @param axiom     input axiom to explain
	 * @param limit     maximum number of explanations to return
	 * @return          the set of axiom sets
	 */
	Set<Set<OWLAxiom>> explain(OWLAxiom axiom, int limit);

	/**
	 * Update the reasoner contents.
	 *
	 * @param additions     axioms to add
	 */
	void insert(Set<OWLAxiom> additions);

	/**
	 * Update the reasoner contents.
	 *
	 * @param removals      axioms to remove
	 */
	void delete(Set<OWLAxiom> removals);

	/**
	 * Classifies the reasoner.
	 */
	void classify();
}