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
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
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
public class LargeDisjunctionPattern extends AxiomLintPattern
{
	private static final LintFormat DEFAULT_LINT_FORMAT = new SimpleLintFormat();

	private int _maxAllowed = 10;
	private final DisjunctionSizeCollector _visitor;

	public LargeDisjunctionPattern()
	{
		_visitor = new DisjunctionSizeCollector();
	}

	@Override
	public String getName()
	{
		return getClass().getSimpleName() + " (MaxAllowed = " + _maxAllowed + ")";
	}

	@Override
	public String getDescription()
	{
		return "Too many disjuncts in a _disjunction - maximum recommended is " + _maxAllowed;
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
		_visitor.reset();
		axiom.accept(_visitor);
		final long disjunctionSize = _visitor.getDisjunctionSize();
		if (disjunctionSize > _maxAllowed)
		{
			final Lint lint = makeLint();
			lint.addParticipatingAxiom(axiom);
			lint.setSeverity(new Severity(disjunctionSize));
			setLint(lint);
		}
	}
}

class DisjunctionSizeCollector extends OWLDeepEntityVisitorAdapter
{
	private long _size;

	public void reset()
	{
		_size = 0;
	}

	public long getDisjunctionSize()
	{
		return _size;
	}

	@Override
	public Collection<OWLEntity> visit(final OWLObjectUnionOf union)
	{
		final long size = union.operands().count();
		if (size > _size)
			_size = size;
		return super.visit(union);
	}
}
