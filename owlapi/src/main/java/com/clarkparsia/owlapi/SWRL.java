// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapi;

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
public class SWRL
{
	public static Set<SWRLAtom> antecedent(final SWRLAtom... atoms)
	{
		return SetUtils.create(atoms);
	}

	public static Set<SWRLAtom> atoms(final SWRLAtom... atoms)
	{
		return SetUtils.create(atoms);
	}

	public static SWRLBuiltInAtom builtIn(final SWRLBuiltInsVocabulary builtIn, final SWRLDArgument... args)
	{
		return OWL.factory.getSWRLBuiltInAtom(builtIn.getIRI(), Arrays.asList(args));
	}

	public static SWRLBuiltInAtom builtIn(final SWRLBuiltInsVocabulary builtIn, final List<SWRLDArgument> args)
	{
		return OWL.factory.getSWRLBuiltInAtom(builtIn.getIRI(), args);
	}

	public static SWRLClassAtom classAtom(final OWLClassExpression desc, final SWRLIArgument arg)
	{
		return OWL.factory.getSWRLClassAtom(desc, arg);
	}

	public static Set<SWRLAtom> consequent(final SWRLAtom... atoms)
	{
		return SetUtils.create(atoms);
	}

	public static SWRLLiteralArgument constant(final boolean constant)
	{
		return OWL.factory.getSWRLLiteralArgument(OWL.constant(constant));
	}

	public static SWRLLiteralArgument constant(final double constant)
	{
		return OWL.factory.getSWRLLiteralArgument(OWL.constant(constant));
	}

	public static SWRLLiteralArgument constant(final float constant)
	{
		return OWL.factory.getSWRLLiteralArgument(OWL.constant(constant));
	}

	public static SWRLLiteralArgument constant(final int constant)
	{
		return OWL.factory.getSWRLLiteralArgument(OWL.constant(constant));
	}

	public static SWRLLiteralArgument constant(final OWLLiteral constant)
	{
		return OWL.factory.getSWRLLiteralArgument(constant);
	}

	public static SWRLLiteralArgument constant(final String constant)
	{
		return OWL.factory.getSWRLLiteralArgument(OWL.constant(constant));
	}

	public static SWRLLiteralArgument constant(final String value, final OWLDatatype datatype)
	{
		return OWL.factory.getSWRLLiteralArgument(OWL.constant(value, datatype));
	}

	public static SWRLLiteralArgument constant(final String value, final String lang)
	{
		return OWL.factory.getSWRLLiteralArgument(OWL.constant(value, lang));
	}

	public static SWRLDataRangeAtom dataRangeAtom(final OWLDataRange rng, final SWRLLiteralArgument arg)
	{
		return OWL.factory.getSWRLDataRangeAtom(rng, arg);
	}

	public static SWRLDifferentIndividualsAtom differentFrom(final SWRLIArgument ind1, final SWRLIArgument ind2)
	{
		return OWL.factory.getSWRLDifferentIndividualsAtom(ind1, ind2);
	}

	public static SWRLVariable variable(final IRI var)
	{
		return OWL.factory.getSWRLVariable(var);
	}

	public static SWRLVariable variable(final String var)
	{
		return OWL.factory.getSWRLVariable(IRI.create(var));
	}

	public static SWRLBuiltInAtom equal(final SWRLDArgument arg1, final SWRLDArgument arg2)
	{
		return OWL.factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.EQUAL.getIRI(), Arrays.asList(arg1, arg2));
	}

	public static SWRLBuiltInAtom greaterThan(final SWRLDArgument arg1, final SWRLDArgument arg2)
	{
		return OWL.factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.GREATER_THAN.getIRI(), Arrays.asList(arg1, arg2));
	}

	public static SWRLBuiltInAtom greaterThanOrEqual(final SWRLDArgument arg1, final SWRLDArgument arg2)
	{
		return OWL.factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.GREATER_THAN_OR_EQUAL.getIRI(), Arrays.asList(arg1, arg2));
	}

	public static SWRLIndividualArgument individual(final OWLIndividual individual)
	{
		return OWL.factory.getSWRLIndividualArgument(individual);
	}

	public static SWRLIndividualArgument individual(final String individual)
	{
		return OWL.factory.getSWRLIndividualArgument(OWL.Individual(individual));
	}

	public static SWRLBuiltInAtom lessThan(final SWRLDArgument arg1, final SWRLDArgument arg2)
	{
		return OWL.factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.LESS_THAN.getIRI(), Arrays.asList(arg1, arg2));
	}

	public static SWRLBuiltInAtom lessThanOrEqual(final SWRLDArgument arg1, final SWRLDArgument arg2)
	{
		return OWL.factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.LESS_THAN_OR_EQUAL.getIRI(), Arrays.asList(arg1, arg2));
	}

	public static SWRLBuiltInAtom notEqual(final SWRLDArgument arg1, final SWRLDArgument arg2)
	{
		return OWL.factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.NOT_EQUAL.getIRI(), Arrays.asList(arg1, arg2));
	}

	public static SWRLDataPropertyAtom propertyAtom(final OWLDataPropertyExpression property, final SWRLIArgument arg0, final SWRLDArgument arg1)
	{
		return OWL.factory.getSWRLDataPropertyAtom(property, arg0, arg1);
	}

	public static SWRLObjectPropertyAtom propertyAtom(final OWLObjectPropertyExpression property, final SWRLIArgument arg0, final SWRLIArgument arg1)
	{
		return OWL.factory.getSWRLObjectPropertyAtom(property, arg0, arg1);
	}

	public static SWRLRule rule(final Set<? extends SWRLAtom> antecendent, final Set<? extends SWRLAtom> consequent)
	{
		return OWL.factory.getSWRLRule(antecendent, consequent);
	}

	public static SWRLRule rule(final IRI uri, final Set<? extends SWRLAtom> antecendent, final Set<? extends SWRLAtom> consequent)
	{
		return OWL.factory.getSWRLRule(antecendent, consequent);
	}

	public static SWRLRule rule(final IRI uri, final boolean anonymous, final Set<? extends SWRLAtom> antecendent, final Set<? extends SWRLAtom> consequent)
	{
		if (anonymous)
			return OWL.factory.getSWRLRule(antecendent, consequent);
		return OWL.factory.getSWRLRule(antecendent, consequent);
	}

	public static SWRLRule rule(final String uri, final Set<? extends SWRLAtom> antecendent, final Set<? extends SWRLAtom> consequent)
	{
		return OWL.factory.getSWRLRule(antecendent, consequent);
	}

	public static SWRLRule rule(final String uri, final boolean anonymous, final Set<? extends SWRLAtom> antecendent, final Set<? extends SWRLAtom> consequent)
	{
		if (anonymous)
			return OWL.factory.getSWRLRule(antecendent, consequent);
		return OWL.factory.getSWRLRule(antecendent, consequent);
	}

	public static SWRLSameIndividualAtom sameAs(final SWRLIArgument ind1, final SWRLIArgument ind2)
	{
		return OWL.factory.getSWRLSameIndividualAtom(ind1, ind2);
	}
}
