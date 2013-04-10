// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapi;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.utils.SetUtils;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.SWRLAtom;
import org.semanticweb.owl.model.SWRLAtomConstantObject;
import org.semanticweb.owl.model.SWRLAtomDObject;
import org.semanticweb.owl.model.SWRLAtomDVariable;
import org.semanticweb.owl.model.SWRLAtomIObject;
import org.semanticweb.owl.model.SWRLAtomIVariable;
import org.semanticweb.owl.model.SWRLAtomIndividualObject;
import org.semanticweb.owl.model.SWRLBuiltInAtom;
import org.semanticweb.owl.model.SWRLClassAtom;
import org.semanticweb.owl.model.SWRLDataRangeAtom;
import org.semanticweb.owl.model.SWRLDataValuedPropertyAtom;
import org.semanticweb.owl.model.SWRLDifferentFromAtom;
import org.semanticweb.owl.model.SWRLObjectPropertyAtom;
import org.semanticweb.owl.model.SWRLRule;
import org.semanticweb.owl.model.SWRLSameAsAtom;
import org.semanticweb.owl.vocab.SWRLBuiltInsVocabulary;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
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
public class SWRL {
	public static Set<SWRLAtom<?>> antecedent(SWRLAtom<?>... atoms) {
		return SetUtils.create( atoms );
	}

	public static Set<SWRLAtom<?>> atoms(SWRLAtom<?>... atoms) {
		return SetUtils.create( atoms );
	}

	public static SWRLBuiltInAtom builtIn(SWRLBuiltInsVocabulary builtIn, SWRLAtomDObject... args) {
		return OWL.factory.getSWRLBuiltInAtom( builtIn, Arrays.asList( args ) );
	}
	
	public static SWRLBuiltInAtom builtIn(SWRLBuiltInsVocabulary builtIn, List<SWRLAtomDObject> args) {
		return OWL.factory.getSWRLBuiltInAtom( builtIn, args );
	}

	public static SWRLClassAtom classAtom(OWLDescription desc, SWRLAtomIObject arg) {
		return OWL.factory.getSWRLClassAtom( desc, arg );
	}

	public static Set<SWRLAtom<?>> consequent(SWRLAtom<?>... atoms) {
		return SetUtils.create( atoms );
	}

	public static SWRLAtomConstantObject constant(boolean constant) {
		return OWL.factory.getSWRLAtomConstantObject( OWL.constant( constant ) );
	}

	public static SWRLAtomConstantObject constant(double constant) {
		return OWL.factory.getSWRLAtomConstantObject( OWL.constant( constant ) );
	}

	public static SWRLAtomConstantObject constant(float constant) {
		return OWL.factory.getSWRLAtomConstantObject( OWL.constant( constant ) );
	}

	public static SWRLAtomConstantObject constant(int constant) {
		return OWL.factory.getSWRLAtomConstantObject( OWL.constant( constant ) );
	}

	public static SWRLAtomConstantObject constant(OWLConstant constant) {
		return OWL.factory.getSWRLAtomConstantObject( constant );
	}

	public static SWRLAtomConstantObject constant(String constant) {
		return OWL.factory.getSWRLAtomConstantObject( OWL.constant( constant ) );
	}

	public static SWRLAtomConstantObject constant(String value, OWLDataType datatype) {
		return OWL.factory.getSWRLAtomConstantObject( OWL.constant( value, datatype ) );
	}

	public static SWRLAtomConstantObject constant(String value, String lang) {
		return OWL.factory.getSWRLAtomConstantObject( OWL.constant( value, lang ) );
	}

	public static SWRLDataRangeAtom dataRangeAtom(OWLDataRange rng, SWRLAtomDObject arg) {
		return OWL.factory.getSWRLDataRangeAtom( rng, arg );
	}

	public static SWRLDifferentFromAtom differentFrom(SWRLAtomIObject ind1, SWRLAtomIObject ind2) {
		return OWL.factory.getSWRLDifferentFromAtom( ind1, ind2 );
	}

	public static SWRLAtomDVariable dVariable(String var) {
		return OWL.factory.getSWRLAtomDVariable( URI.create( var ) );
	}

	public static SWRLAtomDVariable dVariable(URI var) {
		return OWL.factory.getSWRLAtomDVariable( var );
	}

	public static SWRLBuiltInAtom equal(SWRLAtomDObject arg1, SWRLAtomDObject arg2) {
		return OWL.factory.getSWRLBuiltInAtom( SWRLBuiltInsVocabulary.EQUAL, Arrays.asList(
				arg1, arg2 ) );
	}

	public static SWRLBuiltInAtom greaterThan(SWRLAtomDObject arg1, SWRLAtomDObject arg2) {
		return OWL.factory.getSWRLBuiltInAtom( SWRLBuiltInsVocabulary.GREATER_THAN, Arrays.asList(
				arg1, arg2 ) );
	}

	public static SWRLBuiltInAtom greaterThanOrEqual(SWRLAtomDObject arg1, SWRLAtomDObject arg2) {
		return OWL.factory.getSWRLBuiltInAtom( SWRLBuiltInsVocabulary.GREATER_THAN_OR_EQUAL, Arrays.asList(
				arg1, arg2 ) );
	}

	public static SWRLAtomIndividualObject individual(OWLIndividual individual) {
		return OWL.factory.getSWRLAtomIndividualObject( individual );
	}

	public static SWRLAtomIndividualObject individual(String individual) {
		return OWL.factory.getSWRLAtomIndividualObject( OWL.Individual( individual ) );
	}

	public static SWRLAtomIVariable iVariable(String var) {
		return OWL.factory.getSWRLAtomIVariable( URI.create( var ) );
	}

	public static SWRLAtomIVariable iVariable(URI var) {
		return OWL.factory.getSWRLAtomIVariable( var );
	}

	public static SWRLBuiltInAtom lessThan(SWRLAtomDObject arg1, SWRLAtomDObject arg2) {
		return OWL.factory.getSWRLBuiltInAtom( SWRLBuiltInsVocabulary.LESS_THAN, Arrays.asList(
				arg1, arg2 ) );
	}

	public static SWRLBuiltInAtom lessThanOrEqual(SWRLAtomDObject arg1, SWRLAtomDObject arg2) {
		return OWL.factory.getSWRLBuiltInAtom( SWRLBuiltInsVocabulary.LESS_THAN_OR_EQUAL, Arrays.asList(
				arg1, arg2 ) );
	}

	public static SWRLBuiltInAtom notEqual(SWRLAtomDObject arg1, SWRLAtomDObject arg2) {
		return OWL.factory.getSWRLBuiltInAtom( SWRLBuiltInsVocabulary.NOT_EQUAL, Arrays.asList(
				arg1, arg2 ) );
	}

	public static SWRLDataValuedPropertyAtom propertyAtom(OWLDataPropertyExpression property,
			SWRLAtomIObject arg0, SWRLAtomDObject arg1) {
		return OWL.factory.getSWRLDataValuedPropertyAtom( property, arg0, arg1 );
	}

	public static SWRLObjectPropertyAtom propertyAtom(OWLObjectPropertyExpression property,
			SWRLAtomIObject arg0, SWRLAtomIObject arg1) {
		return OWL.factory.getSWRLObjectPropertyAtom( property, arg0, arg1 );
	}

	public static SWRLRule rule(Set<? extends SWRLAtom<?>> antecendent,
			Set<? extends SWRLAtom<?>> consequent) {
		return OWL.factory.getSWRLRule( antecendent, consequent );
	}

	public static SWRLRule rule(URI uri, Set<? extends SWRLAtom<?>> antecendent,
			Set<? extends SWRLAtom<?>> consequent) {
		return OWL.factory.getSWRLRule( uri, antecendent, consequent );
	}
	

	public static SWRLRule rule(URI uri, boolean anonymous, Set<? extends SWRLAtom<?>> antecendent,
			Set<? extends SWRLAtom<?>> consequent) {
		return OWL.factory.getSWRLRule( uri, anonymous, antecendent, consequent );
	}

	public static SWRLRule rule(String uri, Set<? extends SWRLAtom<?>> antecendent,
			Set<? extends SWRLAtom<?>> consequent) {
		return OWL.factory.getSWRLRule( URI.create( uri ), antecendent, consequent );
	}
	

	public static SWRLRule rule(String uri, boolean anonymous, Set<? extends SWRLAtom<?>> antecendent,
			Set<? extends SWRLAtom<?>> consequent) {
		return OWL.factory.getSWRLRule( URI.create( uri ), anonymous, antecendent, consequent );
	}
	
	public static SWRLSameAsAtom sameAs(SWRLAtomIObject ind1, SWRLAtomIObject ind2) {
		return OWL.factory.getSWRLSameAsAtom( ind1, ind2 );
	}
}
