// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.complexible.pellet.client.reasoner;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * Simple reasoner interface for schema reasoning.
 *
 * @author Evren Sirin
 */
public interface SchemaReasoner extends AutoCloseable {
	/**
	 * Execute a schema query.
	 *
	 * @param query schema query
	 * @param input input entity
	 * @param <T> type of return entities
	 * @return queyr result
	 */
	<T extends OWLObject> NodeSet<T> query(SchemaQuery query, OWLLogicalEntity input);

	/**
	 * Return the explanations for the given axiom.
	 *
	 * @param axiom input axiom to explain
	 * @param limit maximum number of explanations to return
	 * @return
	 */
	Set<Set<OWLAxiom>> explain(OWLAxiom axiom, int limit);

	/**
	 * Update the reasoner contents.
	 *
	 * @param additions axioms to add
	 * @param removals axioms to remove
	 */
	void update(Set<OWLAxiom> additions, Set<OWLAxiom> removals);
}