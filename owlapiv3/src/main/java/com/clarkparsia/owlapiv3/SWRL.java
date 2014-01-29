// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapiv3;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.utils.SetUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIArgument;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;

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
	public static Set<SWRLAtom> antecedent(SWRLAtom... atoms) {
		return SetUtils.create( atoms );
	}

	public static Set<SWRLAtom> atoms(SWRLAtom... atoms) {
		return SetUtils.create( atoms );
	}

	public static SWRLBuiltInAtom builtIn(SWRLBuiltInsVocabulary builtIn, SWRLDArgument... args) {
		return OWL.factory.getSWRLBuiltInAtom( builtIn.getIRI(), Arrays.asList( args ) );
	}
	
	public static SWRLBuiltInAtom builtIn(SWRLBuiltInsVocabulary builtIn, List<SWRLDArgument> args) {
		return OWL.factory.getSWRLBuiltInAtom( builtIn.getIRI(), args );
	}

	public static SWRLClassAtom classAtom(OWLClassExpression desc, SWRLIArgument arg) {
		return OWL.factory.getSWRLClassAtom( desc, arg );
	}

	public static Set<SWRLAtom> consequent(SWRLAtom... atoms) {
		return SetUtils.create( atoms );
	}

	public static SWRLLiteralArgument constant(boolean constant) {
		return OWL.factory.getSWRLLiteralArgument( OWL.constant( constant ) );
	}

	public static SWRLLiteralArgument constant(double constant) {
		return OWL.factory.getSWRLLiteralArgument( OWL.constant( constant ) );
	}

	public static SWRLLiteralArgument constant(float constant) {
		return OWL.factory.getSWRLLiteralArgument( OWL.constant( constant ) );
	}

	public static SWRLLiteralArgument constant(int constant) {
		return OWL.factory.getSWRLLiteralArgument( OWL.constant( constant ) );
	}

	public static SWRLLiteralArgument constant(OWLLiteral constant) {
		return OWL.factory.getSWRLLiteralArgument( constant );
	}

	public static SWRLLiteralArgument constant(String constant) {
		return OWL.factory.getSWRLLiteralArgument( OWL.constant( constant ) );
	}

	public static SWRLLiteralArgument constant(String value, OWLDatatype datatype) {
		return OWL.factory.getSWRLLiteralArgument( OWL.constant( value, datatype ) );
	}

	public static SWRLLiteralArgument constant(String value, String lang) {
		return OWL.factory.getSWRLLiteralArgument( OWL.constant( value, lang ) );
	}

	public static SWRLDataRangeAtom dataRangeAtom(OWLDataRange rng, SWRLLiteralArgument arg) {
		return OWL.factory.getSWRLDataRangeAtom( rng, arg );
	}

	public static SWRLDifferentIndividualsAtom differentFrom(SWRLIArgument ind1, SWRLIArgument ind2) {
		return OWL.factory.getSWRLDifferentIndividualsAtom( ind1, ind2 );
	}
	
	public static SWRLVariable variable( IRI var ) {
		return OWL.factory.getSWRLVariable( var );
	}
	
	public static SWRLVariable variable( String var ) {
		return OWL.factory.getSWRLVariable( IRI.create( var ) );
	}

	public static SWRLBuiltInAtom equal(SWRLDArgument arg1, SWRLDArgument arg2) {
		return OWL.factory.getSWRLBuiltInAtom( SWRLBuiltInsVocabulary.EQUAL.getIRI(), Arrays.asList(
				arg1, arg2 ) );
	}

	public static SWRLBuiltInAtom greaterThan(SWRLDArgument arg1, SWRLDArgument arg2) {
		return OWL.factory.getSWRLBuiltInAtom( SWRLBuiltInsVocabulary.GREATER_THAN.getIRI(), Arrays.asList(
				arg1, arg2 ) );
	}

	public static SWRLBuiltInAtom greaterThanOrEqual(SWRLDArgument arg1, SWRLDArgument arg2) {
		return OWL.factory.getSWRLBuiltInAtom( SWRLBuiltInsVocabulary.GREATER_THAN_OR_EQUAL.getIRI(), Arrays.asList(
				arg1, arg2 ) );
	}

	public static SWRLIndividualArgument individual(OWLIndividual individual) {
		return OWL.factory.getSWRLIndividualArgument( individual );
	}

	public static SWRLIndividualArgument individual(String individual) {
		return OWL.factory.getSWRLIndividualArgument( OWL.Individual( individual ) );
	}

	public static SWRLBuiltInAtom lessThan(SWRLDArgument arg1, SWRLDArgument arg2) {
		return OWL.factory.getSWRLBuiltInAtom( SWRLBuiltInsVocabulary.LESS_THAN.getIRI(), Arrays.asList(
				arg1, arg2 ) );
	}

	public static SWRLBuiltInAtom lessThanOrEqual(SWRLDArgument arg1, SWRLDArgument arg2) {
		return OWL.factory.getSWRLBuiltInAtom( SWRLBuiltInsVocabulary.LESS_THAN_OR_EQUAL.getIRI(), Arrays.asList(
				arg1, arg2 ) );
	}

	public static SWRLBuiltInAtom notEqual(SWRLDArgument arg1, SWRLDArgument arg2) {
		return OWL.factory.getSWRLBuiltInAtom( SWRLBuiltInsVocabulary.NOT_EQUAL.getIRI(), Arrays.asList(
				arg1, arg2 ) );
	}

	public static SWRLDataPropertyAtom propertyAtom(OWLDataPropertyExpression property,
			SWRLIArgument arg0, SWRLDArgument arg1) {
		return OWL.factory.getSWRLDataPropertyAtom( property, arg0, arg1 );
	}

	public static SWRLObjectPropertyAtom propertyAtom(OWLObjectPropertyExpression property,
			SWRLIArgument arg0, SWRLIArgument arg1) {
		return OWL.factory.getSWRLObjectPropertyAtom( property, arg0, arg1 );
	}

	public static SWRLRule rule(Set<? extends SWRLAtom> antecendent,
			Set<? extends SWRLAtom> consequent) {
		return OWL.factory.getSWRLRule( antecendent, consequent );
	}

	public static SWRLRule rule(IRI uri, Set<? extends SWRLAtom> antecendent,
			Set<? extends SWRLAtom> consequent) {
		return OWL.factory.getSWRLRule( uri, antecendent, consequent );
	}
	

	public static SWRLRule rule(IRI uri, boolean anonymous, Set<? extends SWRLAtom> antecendent,
			Set<? extends SWRLAtom> consequent) {
		if( anonymous )
			return OWL.factory.getSWRLRule( antecendent, consequent );
		return OWL.factory.getSWRLRule( uri, antecendent, consequent );
	}

	public static SWRLRule rule(String uri, Set<? extends SWRLAtom> antecendent,
			Set<? extends SWRLAtom> consequent) {
		return OWL.factory.getSWRLRule( IRI.create( uri ), antecendent, consequent );
	}
	

	public static SWRLRule rule(String uri, boolean anonymous, Set<? extends SWRLAtom> antecendent,
			Set<? extends SWRLAtom> consequent) {
		if( anonymous )
			return OWL.factory.getSWRLRule( antecendent, consequent );
		return OWL.factory.getSWRLRule( IRI.create( uri ), antecendent, consequent );
	}
	
	public static SWRLSameIndividualAtom sameAs(SWRLIArgument ind1, SWRLIArgument ind2) {
		return OWL.factory.getSWRLSameIndividualAtom( ind1, ind2 );
	}
}
