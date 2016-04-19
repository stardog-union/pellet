// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.value;

import aterm.ATermAppl;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.RuleAtomVisitor;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class RuleAtomAsserter implements RuleAtomVisitor
{
	private ABox abox;
	private CompletionStrategy strategy;

	private VariableBinding binding;
	private DependencySet ds;
	private boolean negated;

	private boolean asserted;

	public RuleAtomAsserter()
	{
	}

	public boolean assertAtom(final RuleAtom atom, final VariableBinding binding, final DependencySet ds, final boolean negated, final ABox abox, final CompletionStrategy strategy)
	{
		asserted = true;

		this.binding = binding;
		this.ds = ds;
		this.negated = negated;
		this.strategy = strategy;
		this.abox = abox;

		atom.accept(this);

		return asserted;
	}

	@Override
	public void visit(final BuiltInAtom atom)
	{
		asserted = false;
	}

	@Override
	public void visit(final ClassAtom atom)
	{
		final ATermAppl cls = atom.getPredicate();
		final ATermAppl ind = binding.get(atom.getArgument()).getName();

		addType(ind, cls);
	}

	private void addType(final ATermAppl ind, ATermAppl cls)
	{
		DependencySet nodeDS = ds;
		Individual node = abox.getIndividual(ind);

		if (node.isMerged())
		{
			nodeDS = node.getMergeDependency(true);
			node = node.getSame();
		}

		if (negated)
			cls = ATermUtils.negate(cls);

		strategy.addType(node, cls, nodeDS);
	}

	private void addEdge(final ATermAppl p, final ATermAppl s, final ATermAppl o)
	{
		DependencySet edgeDS = ds;
		Individual node1 = abox.getIndividual(s);

		if (node1.isMerged())
		{
			edgeDS = node1.getMergeDependency(true);
			node1 = node1.getSame();
		}

		if (negated)
		{
			final ATermAppl cls = all(p, not(value(o)));
			strategy.addType(node1, cls, ds);
		}
		else
		{
			Node node2 = abox.getNode(o);
			if (node2.isMerged())
			{
				edgeDS = node2.getMergeDependency(true);
				node2 = node2.getSame();
			}
			strategy.addEdge(node1, abox.getRole(p), node2, edgeDS);
		}
	}

	@Override
	public void visit(final DataRangeAtom atom)
	{
		asserted = false;
	}

	@Override
	public void visit(final DatavaluedPropertyAtom atom)
	{
		final ATermAppl p = atom.getPredicate();
		final ATermAppl s = binding.get(atom.getArgument1()).getName();
		final ATermAppl o = binding.get(atom.getArgument2()).getName();

		addEdge(p, s, o);
	}

	@Override
	public void visit(final DifferentIndividualsAtom atom)
	{
		final ATermAppl ind1 = binding.get(atom.getArgument1()).getName();
		final ATermAppl ind2 = binding.get(atom.getArgument2()).getName();
		final ATermAppl cls = not(value(ind2));

		addType(ind1, cls);
	}

	@Override
	public void visit(final IndividualPropertyAtom atom)
	{
		final ATermAppl p = atom.getPredicate();
		final ATermAppl s = binding.get(atom.getArgument1()).getName();
		final ATermAppl o = binding.get(atom.getArgument2()).getName();

		addEdge(p, s, o);
	}

	@Override
	public void visit(final SameIndividualAtom atom)
	{
		final ATermAppl ind1 = binding.get(atom.getArgument1()).getName();
		final ATermAppl ind2 = binding.get(atom.getArgument2()).getName();
		final ATermAppl cls = value(ind2);

		addType(ind1, cls);
	}

}
