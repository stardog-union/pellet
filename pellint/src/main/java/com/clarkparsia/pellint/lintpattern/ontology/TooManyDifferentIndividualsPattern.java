// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.lintpattern.ontology;

import com.clarkparsia.pellint.format.CompactClassLintFormat;
import com.clarkparsia.pellint.format.LintFormat;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.model.Severity;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;

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
public class TooManyDifferentIndividualsPattern implements OntologyLintPattern
{
	private static final LintFormat DEFAULT_LINT_FORMAT = new CompactClassLintFormat();

	private int _maxAllowed = 50;

	@Override
	public String getName()
	{
		return getClass().getSimpleName() + " (MaxAllowed = " + _maxAllowed + ")";
	}

	@Override
	public String getDescription()
	{
		return "Too many individuals involved in DifferentIndividuals axioms - maximum recommended is " + _maxAllowed;
	}

	@Override
	public boolean isFixable()
	{
		return false;
	}

	@Override
	public LintFormat getDefaultLintFormat()
	{
		return DEFAULT_LINT_FORMAT;
	}

	public void setMaxAllowed(final int value)
	{
		_maxAllowed = value;
	}

	@Override
	public List<Lint> match(final OWLOntology ontology)
	{
		final long totalEstimatedMemory = ontology//
				.axioms(AxiomType.DIFFERENT_INDIVIDUALS)//
				.map(axiom -> estimateMemoryConcumption(axiom.individuals().count()))//
				.reduce((sum, cost) -> sum + cost)//
				.orElse(0L);

		final List<Lint> allLints = new ArrayList<>();
		if (totalEstimatedMemory > estimateMemoryConcumption(_maxAllowed))
		{
			final Lint lint = new Lint(this, ontology);
			lint.setSeverity(new Severity(totalEstimatedMemory));
			allLints.add(lint);
		}
		return allLints;
	}

	private static long estimateMemoryConcumption(final long individualCount)
	{
		return individualCount * (individualCount - 1);
	}
}
