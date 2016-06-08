// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.lintpattern.ontology;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.pellint.format.CompactClassLintFormat;
import com.clarkparsia.pellint.format.LintFormat;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.model.LintFixer;
import com.clarkparsia.pellint.util.OWLUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

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
 * @author Harris Lin
 */
public class EquivalentAndSubclassAxiomPattern implements OntologyLintPattern
{
	private static final LintFormat DEFAULT_LINT_FORMAT = new CompactClassLintFormat();

	@Override
	public String getName()
	{
		return getClass().getSimpleName();
	}

	@Override
	public String getDescription()
	{
		return "A named concept appears in equivalent axiom(s) and on the left-hand side of a subclass axiom";
	}

	@Override
	public boolean isFixable()
	{
		return true;
	}

	@Override
	public LintFormat getDefaultLintFormat()
	{
		return DEFAULT_LINT_FORMAT;
	}

	@Override
	public List<Lint> match(final OWLOntology ontology)
	{
		final List<Lint> allLints = new ArrayList<>();
		final Iterable<OWLClass> it = ontology.classesInSignature()::iterator;
		for (final OWLClass owlClass : it)
		{
			final Set<OWLEquivalentClassesAxiom> equivalents = ontology.equivalentClassesAxioms(owlClass).collect(Collectors.toSet());
			final Set<OWLSubClassOfAxiom> subclasses = ontology.subClassAxiomsForSubClass(owlClass).collect(Collectors.toSet());

			final Set<OWLEquivalentClassesAxiom> badEquivalents = new HashSet<>();
			for (final OWLEquivalentClassesAxiom equivalent : equivalents)
			{
				final Iterable<OWLClassExpression> expressions = equivalent.classExpressions()::iterator;
				for (final OWLClassExpression desc : expressions)
					if (OWLUtil.isComplex(desc))
					{
						badEquivalents.add(equivalent);
						break;
					}
			}

			if (badEquivalents.isEmpty())
				continue;
			if (badEquivalents.size() == 1 && subclasses.isEmpty())
				continue;

			final Lint lint = new Lint(this, ontology);
			lint.addParticipatingClass(owlClass);
			lint.addAllParticipatingAxioms(badEquivalents);
			lint.addAllParticipatingAxioms(subclasses);
			final Set<OWLClassAxiom> fixedAxioms = fixEquivalentAxioms(owlClass, badEquivalents);
			final LintFixer fixer = new LintFixer(badEquivalents, fixedAxioms);
			lint.setLintFixer(fixer);
			allLints.add(lint);
		}

		return allLints;
	}

	private static Set<OWLClassAxiom> fixEquivalentAxioms(final OWLClass classToFix, final Set<OWLEquivalentClassesAxiom> axioms)
	{
		final Set<OWLClassAxiom> fixes = new HashSet<>();
		for (final OWLEquivalentClassesAxiom axiom : axioms)
		{
			final Set<OWLClassExpression> descs = axiom.classExpressions().collect(Collectors.toSet());
			descs.remove(classToFix);

			if (descs.size() == 1)
				fixes.add(OWL.subClassOf(classToFix, descs.iterator().next()));
			else
			{
				fixes.add(OWL.equivalentClasses(descs));
				fixes.add(OWL.subClassOf(classToFix, OWL.and(descs)));
			}
		}
		return fixes;
	}
}
