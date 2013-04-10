// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena.graph.loader;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Only simple properties can be used in cardinality restrictions,
 * disjointness axioms, irreflexivity and antisymmetry axioms. This enumeration
 * is used to identify why a certain property should be treated as simple
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public enum SimpleProperty {
	SELF("self restriction"), CARDINALITY("cardinality restriction"),
	IRREFLEXIVE("irreflexivity axiom"), ANTI_SYM("antisymmetry axiom"),
	DISJOINT("disjointness axioms");

	private String	description;

	SimpleProperty(String desc) {
		description = desc;
	}

	public String toString() {
		return description;
	}
}