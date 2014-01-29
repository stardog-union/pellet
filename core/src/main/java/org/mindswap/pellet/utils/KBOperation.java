// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils;

/**
 * <p>
 * Title: List of knowledge base operations.
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
public enum KBOperation {
	IS_DIRECT_TYPE, // TODO
	IS_TYPE, // if realized trivial, oth. 1 sat (more frq
	// than hpv, but less than sc)

	HAS_PROPERTY_VALUE, // rare sat (nonempty dependency set of
	// an edge in Compl. G.)

	// use told taxonomy - to be provided by KB - not to classify the whole KB
	IS_SUBCLASS_OF, IS_EQUIVALENT_CLASS, // triv.
	// if
	// classified,
	// otherwise
	// 1 sat

	IS_DISJOINT_WITH, IS_COMPLEMENT_OF, // 1 sat

	IS_SUBPROPERTY_OF, IS_EQUIVALENT_PROPERTY, // triv
	IS_DOMAIN, IS_RANGE,

	IS_OBJECT_PROPERTY, IS_DATATYPE_PROPERTY, // triv

	IS_FUNCTIONAL_PROPERTY, IS_INVERSE_FUNCTIONAL_PROPERTY, IS_TRANSITIVE_PROPERTY, // one
	// sat.
	// check if
	// any

	IS_INVERSE_OF, IS_SYMMETRIC_PROPERTY, GET_INVERSES, // triv.

	IS_ASYMMETRIC_PROPERTY, IS_REFLEXIVE_PROPERTY, IS_IRREFLEXIVE_PROPERTY,
	
	// TODO complexity get instances
	GET_DIRECT_INSTANCES, // TODO
	GET_INSTANCES, // if realized triv, otherwise binary
	// retrieval
	GET_DIRECT_TYPES, // TODO
	GET_TYPES, // if realized triv, otherwise TODO binary
	// class retrieval.
	// Currently, realization
	GET_PROPERTY_VALUE, // binary instance retrieval

	// probably throw away
	IS_SAME_AS, // 1 sat (rare)
	IS_DIFFERENT_FROM, // 1 sat

	GET_SAMES, GET_DIFFERENTS,

	GET_OBJECT_PROPERTIES, GET_DATATYPE_PROPERTIES, // triv.

	GET_FUNCTIONAL_PROPERTIES, GET_INVERSE_FUNCTIONAL_PROPERTIES, GET_TRANSITIVE_PROPERTIES, GET_SYMMETRIC_PROPERTIES, // triv.
	
	GET_ASYMMETRIC_PROPERTIES, GET_REFLEXIVE_PROPERTIES, GET_IRREFLEXIVE_PROPERTIES,
	
	// -
	// not
	// complete
	// impl.
	GET_DOMAINS, GET_RANGES,

	// subClassOf
	GET_SUB_OR_SUPERCLASSES, GET_EQUIVALENT_CLASSES, // triv.
	// if
	// classified
	// and named, otherwise classification
	GET_DIRECT_SUB_OR_SUPERCLASSES, // TODO

	// disjointWithClass
	GET_DISJOINT_CLASSES, GET_COMPLEMENT_CLASSES, // classification

	// subPropertyOf
	GET_SUB_OR_SUPERPROPERTIES, GET_EQUIVALENT_PROPERTIES, // triv.

	GET_DIRECT_SUB_OR_SUPERPROPERTIES, // TODO
}
