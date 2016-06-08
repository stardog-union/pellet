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

	private int _maxRecommended = 10;
	private final CardinalitySizeCollector _Visitor;

	public LargeCardinalityPattern()
	{
		_Visitor = new CardinalitySizeCollector();
	}

	@Override
	public String getName()
	{
		return getClass().getSimpleName() + " (MaxRecommended = " + _maxRecommended + ")";
	}

	@Override
	public String getDescription()
	{
		return "Cardinality restriction is too large - maximum recommended is " + _maxRecommended;
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
		_maxRecommended = value;
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
		_Visitor.reset();
		axiom.accept(_Visitor);
		final int cardinalitySize = _Visitor.getCardinalitySize();
		if (cardinalitySize > _maxRecommended)
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
	private int _size;

	public void reset()
	{
		_size = 0;
	}

	public int getCardinalitySize()
	{
		return _size;
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
		if (size > _size)
			_size = size;
	}
}
