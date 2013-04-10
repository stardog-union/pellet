// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

/**
 * <p>
 * Title: Query Predicates
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Petr Kremen
 */

public enum QueryPredicate {
	Type, PropertyValue, NegativePropertyValue, SameAs, DifferentFrom, SubClassOf, EquivalentClass,
	DisjointWith, ComplementOf, EquivalentProperty, SubPropertyOf, InverseOf, ObjectProperty,
	DatatypeProperty, Domain, Range, Functional, InverseFunctional, Transitive, Symmetric, Asymmetric, 
	Reflexive, Irreflexive, propertyDisjointWith, Annotation,

	Datatype,
	
	// SPARQL-DL non-monotonic extensions
	StrictSubClassOf, DirectSubClassOf, DirectType, DirectSubPropertyOf, StrictSubPropertyOf,
	NotKnown,

	// Union query
	Union,

	// undistinguished variable core
	UndistVarCore;

	private QueryPredicate() {
	}

	@Override
	public String toString() {
		return name();
	}
}