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
	 * Enumeration of query types for schema reasoner.
	 *
	 * @author Evren Sirin
	 */
	enum QueryType {
		/**
		 * Query to get equivalents (equivalent class, equivalent property)
		 */
		EQUIVALENT,

		/**
		 * Query to get children in the hierarchy (direct subclass, direct subproperty)
		 */
		CHILD,

		/**
		 * Query to get parents in the hierarchy (direct superclass, direct superproperty)
		 */
		PARENT,

		/**
		 * Query to get descendants in the hierarchy (all subclass, all subproperty)
		 */
		DESCENDANT,

		/**
		 * Query to get ancestors in the hierarchy (all superclass, all superproperty)
		 */
		ANCESTOR,

		/**
		 * Query to get disjoints (disjoint class, disjoint property)
		 */
		DISJOINT,

		/**
		 * Query to get inverse properties
		 */
		INVERSE,

		/**
		 * Query to get property domains
		 */
		DOMAIN,

		/**
		 * Query to get property ranges
		 */
		RANGE
	}

	/**
	 * Execute a schema query.
	 *
	 * @param theQueryType  schema query
	 * @param input         input entity
	 * @param <T>           type of return entities
	 * @return              the query result
	 */
	<T extends OWLObject> NodeSet<T> query(QueryType theQueryType, OWLLogicalEntity input);

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
	 * @param removals      axioms to remove
	 */
	void update(Set<OWLAxiom> additions, Set<OWLAxiom> removals);
}