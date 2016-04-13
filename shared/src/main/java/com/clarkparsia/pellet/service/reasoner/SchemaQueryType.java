// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.service.reasoner;

/**
 * Enumeration of query types for schema queries.
 *
 * @author Evren Sirin
 */
public enum SchemaQueryType {
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
