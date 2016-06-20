// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import com.clarkparsia.pellet.rules.model.AtomDConstant;
import com.clarkparsia.pellet.rules.model.AtomDObject;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.AtomObject;
import com.clarkparsia.pellet.rules.model.AtomObjectVisitor;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.RuleAtomVisitor;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;
import openllet.aterm.ATermAppl;
import org.mindswap.pellet.utils.ATermUtils;

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
public class RulesToATermTranslator implements RuleAtomVisitor, AtomObjectVisitor
{
	private ATermAppl term;

	public ATermAppl translate(final Rule rule)
	{
		term = null;
		visit(rule);
		return term;
	}

	public ATermAppl translate(final RuleAtom ruleAtom)
	{
		term = null;
		ruleAtom.accept(this);
		return term;
	}

	public ATermAppl translate(final AtomObject obj)
	{
		term = null;
		obj.accept(this);
		return term;
	}

	public void visit(final Rule rule)
	{
		final ATermAppl[] head = new ATermAppl[rule.getHead().size()];
		final ATermAppl[] body = new ATermAppl[rule.getBody().size()];

		int i = 0;
		for (final RuleAtom atom : rule.getHead())
			head[i++] = translate(atom);

		i = 0;
		for (final RuleAtom atom : rule.getBody())
			body[i++] = translate(atom);

		term = ATermUtils.makeRule(rule.getName(), head, body);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final BuiltInAtom atom)
	{
		final int arity = atom.getAllArguments().size();
		final ATermAppl[] args = new ATermAppl[arity + 1];
		args[0] = ATermUtils.makeTermAppl(atom.getPredicate());
		int i = 1;
		for (final AtomDObject arg : atom.getAllArguments())
			args[i++] = translate(arg);

		term = ATermUtils.makeBuiltinAtom(args);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final ClassAtom atom)
	{
		final ATermAppl c = atom.getPredicate();
		final ATermAppl i = translate(atom.getArgument());

		term = ATermUtils.makeTypeAtom(i, c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final DataRangeAtom atom)
	{
		final ATermAppl d = atom.getPredicate();
		final ATermAppl l = translate(atom.getArgument());

		term = ATermUtils.makeTypeAtom(l, d);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final DatavaluedPropertyAtom atom)
	{
		final ATermAppl p = atom.getPredicate();
		final ATermAppl s = translate(atom.getArgument1());
		final ATermAppl o = translate(atom.getArgument2());

		term = ATermUtils.makePropAtom(p, s, o);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final DifferentIndividualsAtom atom)
	{
		final ATermAppl t1 = translate(atom.getArgument1());
		final ATermAppl t2 = translate(atom.getArgument2());

		term = ATermUtils.makeDifferent(t1, t2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final IndividualPropertyAtom atom)
	{
		final ATermAppl p = atom.getPredicate();
		final ATermAppl s = translate(atom.getArgument1());
		final ATermAppl o = translate(atom.getArgument2());

		term = ATermUtils.makePropAtom(p, s, o);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final SameIndividualAtom atom)
	{
		final ATermAppl t1 = translate(atom.getArgument1());
		final ATermAppl t2 = translate(atom.getArgument2());

		term = ATermUtils.makeSameAs(t1, t2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final AtomDConstant constant)
	{
		term = constant.getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final AtomDVariable variable)
	{
		term = ATermUtils.makeVar(variable.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final AtomIConstant constant)
	{
		term = constant.getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final AtomIVariable variable)
	{
		term = ATermUtils.makeVar(variable.getName());
	}

}
