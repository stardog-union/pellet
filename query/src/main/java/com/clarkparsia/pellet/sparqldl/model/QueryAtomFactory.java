// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.mindswap.pellet.KnowledgeBase;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Factory for creating query atoms.
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
public class QueryAtomFactory {

	// ABOX atoms
	public static QueryAtom TypeAtom(final ATermAppl iA, final ATermAppl cA) {
		return new QueryAtomImpl(QueryPredicate.Type, iA, cA);
	}	
	
	public static QueryAtom DatatypeAtom(final ATermAppl lA, final ATermAppl dA) {
		return new QueryAtomImpl(QueryPredicate.Datatype, lA, dA);
	}

	public static QueryAtom PropertyValueAtom(final ATermAppl iA,
			final ATermAppl pA, final ATermAppl ilA) {
		return new QueryAtomImpl(QueryPredicate.PropertyValue, iA, pA, ilA);
	}

	public static QueryAtom NegativePropertyValueAtom(final ATermAppl iA,
			final ATermAppl pA, final ATermAppl ilA) {
		return new QueryAtomImpl(QueryPredicate.NegativePropertyValue, iA, pA, ilA);
	}
	
	public static QueryAtom SameAsAtom(final ATermAppl iA1, final ATermAppl iA2) {
		return new QueryAtomImpl(QueryPredicate.SameAs, iA1, iA2);
	}

	public static QueryAtom DifferentFromAtom(final ATermAppl iA1,
			final ATermAppl iA2) {
		return new QueryAtomImpl(QueryPredicate.DifferentFrom, iA1, iA2);
	}

	// TBOX atoms
	public static QueryAtom SubClassOfAtom(final ATermAppl cA1,
			final ATermAppl cA2) {
		return new QueryAtomImpl(QueryPredicate.SubClassOf, cA1, cA2);
	}

	public static QueryAtom EquivalentClassAtom(final ATermAppl classArgument,
			final ATermAppl classArgument2) {
		return new QueryAtomImpl(QueryPredicate.EquivalentClass,
				classArgument, classArgument2);
	}

	public static QueryAtom DisjointWithAtom(final ATermAppl cA1,
			final ATermAppl cA2) {
		return new QueryAtomImpl(QueryPredicate.DisjointWith, cA1, cA2);
	}

	public static QueryAtom ComplementOfAtom(final ATermAppl cA1,
			final ATermAppl cA2) {
		return new QueryAtomImpl(QueryPredicate.ComplementOf, cA1, cA2);
	}

	// RBOX atoms
	public static QueryAtom SubPropertyOfAtom(final ATermAppl pA1,
			final ATermAppl pA2) {
		return new QueryAtomImpl(QueryPredicate.SubPropertyOf, pA1, pA2);
	}

	public static QueryAtom EquivalentPropertyAtom(final ATermAppl pA1,
			final ATermAppl pA2) {
		return new QueryAtomImpl(QueryPredicate.EquivalentProperty, pA1,
				pA2);
	}
	
	public static QueryAtom DomainAtom(final ATermAppl pA1,
			final ATermAppl cA2) {
		return new QueryAtomImpl(QueryPredicate.Domain, pA1, cA2);
	}

	public static QueryAtom RangeAtom(final ATermAppl pA1,
			final ATermAppl cA2) {
		return new QueryAtomImpl(QueryPredicate.Range, pA1, cA2);
	}

	public static QueryAtom InverseOfAtom(final ATermAppl pA1,
			final ATermAppl pA2) {
		return new QueryAtomImpl(QueryPredicate.InverseOf, pA1, pA2);
	}

	public static QueryAtom ObjectPropertyAtom(final ATermAppl pA) {
		return new QueryAtomImpl(QueryPredicate.ObjectProperty, pA);
	}

	public static QueryAtom DatatypePropertyAtom(final ATermAppl pA) {
		return new QueryAtomImpl(QueryPredicate.DatatypeProperty, pA);
	}

	public static QueryAtom FunctionalAtom(final ATermAppl pA) {
		return new QueryAtomImpl(QueryPredicate.Functional, pA);
	}

	public static QueryAtom InverseFunctionalAtom(final ATermAppl pA) {
		return new QueryAtomImpl(QueryPredicate.InverseFunctional, pA);
	}

	public static QueryAtom TransitiveAtom(final ATermAppl pA) {
		return new QueryAtomImpl(QueryPredicate.Transitive, pA);
	}

	public static QueryAtom SymmetricAtom(final ATermAppl pA) {
		return new QueryAtomImpl(QueryPredicate.Symmetric, pA);
	}

	public static QueryAtom AsymmetricAtom(final ATermAppl pA) {
		return new QueryAtomImpl(QueryPredicate.Asymmetric, pA);
	}
	
	public static QueryAtom ReflexiveAtom(final ATermAppl pA) {
		return new QueryAtomImpl(QueryPredicate.Reflexive, pA);
	}
	
	public static QueryAtom IrreflexiveAtom(final ATermAppl pA) {
		return new QueryAtomImpl(QueryPredicate.Irreflexive, pA);
	}
	
	public static QueryAtom PropertyDisjointWithAtom(final ATermAppl pA1,
			final ATermAppl pA2) {
		return new QueryAtomImpl(QueryPredicate.propertyDisjointWith, pA1, pA2);
	}
	
	public static QueryAtom AnnotationAtom(final ATermAppl iA,
			final ATermAppl pA, final ATermAppl ilA) {
		return new QueryAtomImpl(QueryPredicate.Annotation, iA, pA, ilA);
	}

	// SPARQL-DL nonmonotonic extension
	public static QueryAtom StrictSubClassOfAtom(final ATermAppl c1,
			final ATermAppl c2) {
		return new QueryAtomImpl(QueryPredicate.StrictSubClassOf, c1, c2);
	}

	public static QueryAtom DirectSubClassOfAtom(final ATermAppl c1,
			final ATermAppl c2) {
		return new QueryAtomImpl(QueryPredicate.DirectSubClassOf, c1, c2);
	}

	public static QueryAtom DirectSubPropertyOfAtom(final ATermAppl c1,
			final ATermAppl c2) {
		return new QueryAtomImpl(QueryPredicate.DirectSubPropertyOf, c1, c2);
	}

	public static QueryAtom StrictSubPropertyOfAtom(final ATermAppl c1,
			final ATermAppl c2) {
		return new QueryAtomImpl(QueryPredicate.StrictSubPropertyOf, c1, c2);
	}

	public static QueryAtom DirectTypeAtom(final ATermAppl i, final ATermAppl c) {
		return new QueryAtomImpl(QueryPredicate.DirectType, i, c);
	}

	// core of undistinguished variables
	public static QueryAtom Core(final Collection<QueryAtom> atoms, final Collection<ATermAppl> uv, 
			final KnowledgeBase kb) {
		return new CoreNewImpl(atoms, uv, kb);
	}
	
	public static QueryAtom NotKnownAtom(QueryAtom atom) {
		return new NotKnownQueryAtom( atom );
	}
	
	
	public static QueryAtom NotKnownAtom(QueryAtom... atoms) {
		return new NotKnownQueryAtom( Arrays.asList( atoms ) );
	}
	
	public static QueryAtom NotKnownAtom(List<QueryAtom> atoms) {
		return new NotKnownQueryAtom( atoms );
	}	
	
	public static QueryAtom UnionAtom(List<List<QueryAtom>> union) {
		return new UnionQueryAtom( union );
	}
}
