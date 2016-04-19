// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.lintpattern.axiom;

import com.clarkparsia.pellint.format.LintFormat;
import com.clarkparsia.pellint.format.SimpleLintFormat;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.model.Severity;
import com.clarkparsia.pellint.util.OWLDeepEntityVisitorAdapter;
import java.util.Collection;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
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
public class LargeCardinalityPattern extends AxiomLintPattern
{
	private static final LintFormat DEFAULT_LINT_FORMAT = new SimpleLintFormat();

	private int m_MaxAllowed = 10;
	private final CardinalitySizeCollector m_Visitor;

	public LargeCardinalityPattern()
	{
		m_Visitor = new CardinalitySizeCollector();
	}

	@Override
	public String getName()
	{
		return getClass().getSimpleName() + " (MaxAllowed = " + m_MaxAllowed + ")";
	}

	@Override
	public String getDescription()
	{
		return "Cardinality restriction is too large - maximum recommended is " + m_MaxAllowed;
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
		m_MaxAllowed = value;
	}

	@Override
	public void visit(final OWLDisjointClassesAxiom axiom)
	{
		visitNaryClassAxiom(axiom);
	}

	@Override
	public void visit(final OWLDisjointUnionAxiom axiom)
	{
		visitNaryClassAxiom(axiom);
	}

	@Override
	public void visit(final OWLEquivalentClassesAxiom axiom)
	{
		visitNaryClassAxiom(axiom);
	}

	@Override
	public void visit(final OWLSubClassOfAxiom axiom)
	{
		visitNaryClassAxiom(axiom);
	}

	private void visitNaryClassAxiom(final OWLClassAxiom axiom)
	{
		m_Visitor.reset();
		axiom.accept(m_Visitor);
		final int cardinalitySize = m_Visitor.getCardinalitySize();
		if (cardinalitySize > m_MaxAllowed)
		{
			final Lint lint = makeLint();
			lint.addParticipatingAxiom(axiom);
			lint.setSeverity(new Severity(cardinalitySize));
			setLint(lint);
		}
	}
}

class CardinalitySizeCollector extends OWLDeepEntityVisitorAdapter
{
	private int m_Size;

	public void reset()
	{
		m_Size = 0;
	}

	public int getCardinalitySize()
	{
		return m_Size;
	}

	@Override
	public Collection<OWLEntity> visit(final OWLObjectExactCardinality card)
	{
		process(card);
		return super.visit(card);
	}

	@Override
	public Collection<OWLEntity> visit(final OWLObjectMaxCardinality card)
	{
		process(card);
		return super.visit(card);
	}

	@Override
	public Collection<OWLEntity> visit(final OWLObjectMinCardinality card)
	{
		process(card);
		return super.visit(card);
	}

	protected void process(final OWLObjectCardinalityRestriction card)
	{
		final int size = card.getCardinality();
		if (size > m_Size)
			m_Size = size;
	}
}
